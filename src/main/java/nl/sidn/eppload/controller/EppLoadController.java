package nl.sidn.eppload.controller;

import lombok.RequiredArgsConstructor;
import nl.sidn.eppload.connection.EPPConnection;
import nl.sidn.eppload.dto.Job;
import nl.sidn.eppload.dto.Scenario;
import nl.sidn.eppload.service.EppExecutorService;
import nl.sidn.eppload.service.JobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class EppLoadController {

  private final JobRunner jobRunner;
  EppCommands eppCommands = new EppCommands();


  private void validateRequest(Job job) {
    if (job.getName()==null || job.getName().isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "job name is mandatory");
    }
    if (job.getPort()==null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "port is mandatory");
    }
    if (job.getHostName()==null || job.getHostName().isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "hostname is mandatory");
    }
  }

  @PostMapping(value="/run-job")
  public void runJob(@RequestBody Job job) {
    validateRequest(job);
    jobRunner.addJob(job);
  }

  @PostMapping(value="/stop-job")
  public void stopJob(@RequestBody Job job) {
    jobRunner.removeJob(job);
  }
  @GetMapping(value="/contactcreate")
  public String getContactCreateCommand() {
    return eppCommands.getContactCreate("[commandParameters.contactInfo.name]","[commandParameters.contactInfo.street]", "[commandParameters.contactInfo.city]", "[commandParameters.contactInfo.postalCode]", "[commandParameters.contactInfo.telephone]", "[commandParameters.contactInfo.email]" );
  }

  @GetMapping(value="/contactinfo")
  public String getContactInfoCommand() {
    return eppCommands.getContactInfo("[commandParameters.contactInfo.handle]" );
  }

  @GetMapping(value="/contactupdate")
  public String getContactUpdateCommand() {
    return eppCommands.getContactUpdate("[commandParameters.contactInfo.name]","[commandParameters.contactInfo.street]", "[commandParameters.contactInfo.city]", "[commandParameters.contactInfo.postalCode]", "[commandParameters.contactInfo.telephone]", "[commandParameters.contactInfo.email]" );
  }

  @GetMapping(value="/contactdelete")
  public String getContactDeleteCommand() {
    return eppCommands.getContactDelete("[commandParameters.contactInfo.handle]" );
  }

  @GetMapping(value="/domaincreate")
  public String getDomainCreateCommand() {
    return eppCommands.getDomainCreate("[commandParameters.domainName]","[commandParameters.hostName]", "[commandParameters.hostName2]", "[commandParameters.registrant]", "[commandParameters.adminC]", "[commandParameters.techC]" );
  }

  @GetMapping(value="/domaininfo")
  public String getDomainInfoCommand() {
    return eppCommands.getDomainInfo("[commandParameters.domainName]");
  }

  @GetMapping(value="/domainupdate")
  public String getDomainUpdateCommand() {
    return eppCommands.getDomainUpdate("[commandParameters.domainName]","[commandParameters.hostName]", "[commandParameters.hostName2]", "[commandParameters.registrant]", "[commandParameters.adminC]", "[commandParameters.techC]" );
  }

  @GetMapping(value="/domaindelete")
  public String getDomainDeleteCommand() {
    return eppCommands.getDomainDelete("[commandParameters.domainName]");
  }

  @GetMapping(value="/domaincanceldelete")
  public String getDomainCancelDeleteCommand() {
    return eppCommands.getDomainCancelDelete("[commandParameters.domainName]");
  }

  @GetMapping(value="/domaintransfer")
  public String getDomainTransferCommand() {
    return eppCommands.getDomainTransfer("[commandParameters.domainName]", "[commandParameters.domainToken]");
  }

  @GetMapping(value="/domainrenew")
  public String getDomainRenewCommand() {
    return eppCommands.getDomainRenew("[commandParameters.domainName]", "[commandParameters.renewPeriod]");
  }

  @GetMapping(value="/hostcreate")
  public String getHostCreateCommand() {
    return eppCommands.getHostCreate("[commandParameters.hostName]", "[commandParameters.ipAddress]");
  }

  @GetMapping(value="/hostinfo")
  public String getHostInfoCommand() {
    return eppCommands.getHostInfo("[commandParameters.hostName]");
  }

  @GetMapping(value="/hostupdate")
  public String getHostUpdateCommand() {
    return eppCommands.getHostUpdate("[commandParameters.hostName]", "[commandParameters.ipAddressAdd]", "[commandParameters.ipAddressRemove]", "[commandParameters.hostNameNew]");
  }

  @GetMapping(value="/hostdelete")
  public String getHostDeleteCommand() {
    return eppCommands.getHostDelete("[commandParameters.hostName]");
  }

  @GetMapping(value="/poll")
  public String getPollCommand() {
    return eppCommands.getPoll();
  }

  @GetMapping(value="/")
  public String getAllCommands() {
    return
        "GET /[command] -> Show the structure of the EPP command" +
        "\nPOST /execute-epp -> Runs epp playbook (see EppPlaybook below for JSON body)" +
        "\n\n- [name]++1 means a number will be added each iteration (starting from 1) \n- When a contact is created, the generated handle will be used in subsequent calls." +
        "\n- When a domain is created, the generated token will be used in subsequent calls." +
        "\n\ncommands: \n\nCONTACTCREATE\nCONTACTINFO\nCONTACTUPDATE\nCONTACTDELETE\nDOMAINCREATE\nDOMAININFO\nDOMAINUPDATE\nDOMAINDELETE\nDOMAINCANCELDELETE\nDOMAINTRANSFER\nDOMAINRENEW\nHOSTCREATE\nHOSTINFO\nHOSTUPDATE\nHOSTDELETE\nPOLL\n\n" +
        new Scenario().toString();
  }

}
