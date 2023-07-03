package com.peterpopma.drsload.controller;

import com.peterpopma.drsload.dto.HostAddr;
import com.peterpopma.drsload.dto.PeriodObject;
import com.peterpopma.drsload.dto.Unit;
import lombok.RequiredArgsConstructor;
import com.peterpopma.drsload.dto.Job;
import com.peterpopma.drsload.service.JobRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class LoadController {

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
    return eppCommands.getDomainCreate("[commandParameters.domainName]", Arrays.asList("[commandParameters.hostName]"), "[commandParameters.hostName2]", "[commandParameters.registrant]", Arrays.asList("[commandParameters.techC]") );
  }

  @GetMapping(value="/domaininfo")
  public String getDomainInfoCommand() {
    return eppCommands.getDomainInfo("[commandParameters.domainName]");
  }

  @GetMapping(value="/domainupdate")
  public String getDomainUpdateCommand() {
    return eppCommands.getDomainUpdate("[commandParameters.domainName]",Arrays.asList("[commandParameters.hostName]"), "[commandParameters.hostName2]", "[commandParameters.registrant]", Arrays.asList("[commandParameters.techC]") );
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
    return eppCommands.getDomainRenew("[commandParameters.domainName]", new PeriodObject(Unit.Y, 1) );
  }

  @GetMapping(value="/hostcreate")
  public String getHostCreateCommand() {
    return eppCommands.getHostCreate("[commandParameters.hostName]", Arrays.asList(new HostAddr("192.3.2.1", "ns1.peter.nl")));
  }

  @GetMapping(value="/hostinfo")
  public String getHostInfoCommand() {
    return eppCommands.getHostInfo("[commandParameters.hostName]");
  }

  @GetMapping(value="/hostupdate")
  public String getHostUpdateCommand() {
    return eppCommands.getHostUpdate("[commandParameters.hostName]", Arrays.asList(new HostAddr("192.3.2.1", "ns1.peter.nl")), "[commandParameters.hostNameNew]");
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
    return "With this tool you can generate EPP commands.\n" +
        "It can be used to create testsets, L/P tests, stresstests, etc.\n" +
        "\n" +
        "Usage:\n" +
        "\n" +
        "POST /run-job -> Runs a loadtest (with scenario's in the JSON body)\n" +
        "POST /stop-job -> Stops a loadtest (with job name in the body)\n" +
        "GET /[command] -> Show the template for the epp command (CONTACTCREATE, DOMAINDELETE, etc.)\n" +
        "\n" +
        "You specify a job as json. For example:\n" +
        "\n" +
        "{\n" +
        "    \"name\": \"initial fill\",\n" +
        "\t  \"hostName\": \"localhost\",\n" +
        "\t  \"port\": 700,\n" +
        "\t  \"runTimeSeconds\": 30,\n" +
        "\t  \"scenarioNames\": [ \"domaincreate_domain_0_to_300\" ]\n" +
        "}\n" +
        "\n" +
        "The scenario's must be specified as JSON and must be put in a filename\n" +
        "with exactly the same name, appended with \".json\" (so in the example \"domaincreate_domain_0_to_300.json\")\n" +
        "The location of the scenario files is determined in application.yml:\n" +
        "\n" +
        "eppload:\n" +
        "  scenarios:\n" +
        "    location: C:\\Projects\\EppLoad\\scenarios\n" +
        "\n" +
        "Some example scenarios are specified as .json files in the \"scenarios\" folder in the source repository.\n" +
        "The scenarios are run in parallel, but the commands of one scenario are waiting for response before\n" +
        "executing the next command.\n" +
        "If you don't specify any scenario's, the job default.json is used (but with hostname and port specified in http POST)\n" +
        "\n" +
        "EPP User accounts:\n" +
        "The loadtest assumes you have defined the following 1000 EPP-users:\n" +
        "800000 800001 800002 ... 800999\n" +
        "All these users will user the password \"Secret_123!\"\n" +
        "\n" +
        "Scenario syntax:\n" +
        "\n" +
        "- A value of -1 for \"repeatCount\" or \"runTimeSeconds\" means forever.\n" +
        "- ++[value] means an increasing value, starting at [value] and increased by 1 on each call.\n" +
        "- ??[value] means an random value, from 0 to [value] (value excluded).\n" +
        "- If you leave out a domainname, contacthandle or hostname in an UPDATE, INFO, DELETE, or TRANSFER, the last created object from the scenario is used.\n" +
        "You can use that to create a chain like CONTACT: CREATE > INFO > UPDATE > DELETE > INFO\n" +
        "- When you execute a DOMAININFO command, the token will be used in a subsequent DOMAINTRANSFER call. This can be used to transfer a domain.\n" +
        "- When a domain is created, you can specify the techc, adminc, or registrant handle. \n" +
        "If you leave out some of this values, the {contactInfo} section will be used to create a new contact and couple it instead.\n" +
        "\n" +
        "\n" +
        "commands: \n" +
        "\n" +
        "CONTACTCREATE\n" +
        "CONTACTINFO\n" +
        "CONTACTUPDATE\n" +
        "CONTACTDELETE\n" +
        "DOMAINCREATE\n" +
        "DOMAININFO\n" +
        "DOMAINUPDATE\n" +
        "DOMAINDELETE\n" +
        "DOMAINCANCELDELETE\n" +
        "DOMAINTRANSFER\n" +
        "DOMAINTRANSFERQUERY\n" +
        "DOMAINRENEW\n" +
        "HOSTCREATE\n" +
        "HOSTINFO\n" +
        "HOSTUPDATE\n" +
        "HOSTDELETE\n" +
        "POLL\n" +
        "\n";
  }

}
