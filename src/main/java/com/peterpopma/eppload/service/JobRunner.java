package com.peterpopma.eppload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peterpopma.eppload.connection.EPPConnection;
import com.peterpopma.eppload.controller.DynamicValuesWrapper;
import com.peterpopma.eppload.controller.EppCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.peterpopma.eppload.dto.Command;
import com.peterpopma.eppload.dto.Job;
import com.peterpopma.eppload.dto.Parameters;
import com.peterpopma.eppload.dto.Scenario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobRunner {
  private final EppExecutorService eppExecutorService;
  EppCommands eppCommands = new EppCommands();
  List<Job> jobs = new ArrayList<Job>();

  @Value("${eppload.scenarios.location}")
  String scenariosLocation;

  public void addJob(Job newJob) {

    // create connection
    newJob.setEppConnection(new EPPConnection());
    newJob.getEppConnection().connect(newJob.getHostName(), newJob.getPort());
    newJob.setTimeStarted(Instant.now());
    newJob.setTimeLastDoubleLoad(Instant.now());
    newJob.setDoubleLoadMultiplier(1);
    newJob.setScenarios(new ArrayList<>());

    for (String scenarioName : newJob.getScenarioNames()) {
      Scenario scenario = null;

      // read scenario from disk
      ObjectMapper mapper = new ObjectMapper();

      // read JSON file
      try {
        scenario = mapper.readValue(new File(scenariosLocation + File.separator + scenarioName + ".json"), Scenario.class);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // convert calls per minutes to interval (is easier to use internally)
      scenario.setRunIntervalSeconds(1 / (scenario.getCallsPerMinute() / 60.0f));
      scenario.setTimesRun(1);
      scenario.setTimeLastRun(Instant.now());

      if (scenario.getInitialLogin()!=null) {
        if (!scenario.getInitialLogin().isEmpty()) {
          List<String> userPassword = Stream.of(scenario.getInitialLogin())
              .map(w -> w.split(":")).flatMap(Arrays::stream)
              .collect(Collectors.toList());

          newJob.getEppConnection().readWrite(eppCommands.getLogin(userPassword.get(0), userPassword.get(1)));
        }
      }

      convertDomainCreates(scenario);
      newJob.getScenarios().add(scenario);
    }

    jobs.add(newJob);
  }

  private void convertDomainCreates(Scenario scenario) {
    List<Command> convertedCommands = new ArrayList<>();
    for (Command command : scenario.getCommands() ) {
      if (command.getCommand().equals("DOMAINCREATE")) {
        if (command.getParameters().getContactInfo()!=null) {
          Command extraCommand = new Command();
          extraCommand.setCommand("CONTACTCREATE");
          Parameters parameters = new Parameters();
          parameters.setContactInfo(command.getParameters().getContactInfo());
          extraCommand.setParameters(parameters);
          convertedCommands.add(extraCommand);
        }
      }
      convertedCommands.add(command);
    }

    scenario.setCommands(convertedCommands);
  }

  public void removeJob(Job job) {
    for (Scenario scenario : job.getScenarios()) {
      if (!scenario.getInitialLogin().isEmpty()) {
        job.getEppConnection().readWrite(eppCommands.getLogout());
      }
    }
    job.getEppConnection().disConnect();
    jobs.remove(job);
  }

  @Scheduled(fixedRate = 1000)
  private void runJobs() {

    while (true) {

      for (Job job : jobs) {

        String response = "";

        if (Duration.between(Instant.now(), job.getTimeStarted()).toSeconds()>job.getRunTimeSeconds()) {
          removeJob(job);
        }

        if (job.getScenarios().isEmpty()) {
          // all scenarios have been finished, so we can remove job
          removeJob(job);
        }

        if (job.getDoubleLoadSeconds()!=null) {
          if (Duration.between(Instant.now(), job.getTimeLastDoubleLoad()).toSeconds()>job.getDoubleLoadSeconds()) {
            job.setTimeLastDoubleLoad(Instant.now());
            job.setDoubleLoadMultiplier(job.getDoubleLoadMultiplier() * 2);
          }
        }

        for (Scenario scenario : job.getScenarios()) {

          if (scenario.getRepeatCount()!=-1 && scenario.getTimesRun()>scenario.getRepeatCount()) {
            job.getScenarios().remove(scenario);
          }

          // Determine if scenario must be run in this second and how many times
          float timeForEachCall = scenario.getRunIntervalSeconds() / job.getDoubleLoadMultiplier();
          Duration timePassed = Duration.between(scenario.getTimeLastRun(), Instant.now());
          int callsThisSecond = (int)((timePassed.toNanos()/1_000_000_000)/timeForEachCall);
          scenario.setTimeLastRun(scenario.getTimeLastRun().plusNanos((long)(1_000_000_000 * scenario.getRunIntervalSeconds()/job.getDoubleLoadMultiplier()*callsThisSecond)));

          DynamicValuesWrapper dynamicValues = new DynamicValuesWrapper();

          for (int i = 0; i < callsThisSecond; i++) {
            CompletableFuture<String> cf = eppExecutorService.executeScenario(dynamicValues, scenario, job.getEppConnection());

            try {
              response = cf.get();
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (ExecutionException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
}