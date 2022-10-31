package nl.sidn.drs.epplg.controller;

import lombok.extern.slf4j.Slf4j;
import nl.sidn.drs.epplg.connection.EPPConnection;
import nl.sidn.drs.epplg.dto.EppPlaybook;
import nl.sidn.drs.epplg.service.EppExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class EppLoadController {
  @Autowired
  EppExecutorService eppExecutorService;
  EppCommands eppCommands = new EppCommands();

  String eppHost = "localhost";
  int eppPort = 700;

  private void validateRequest(EppPlaybook eppPlaybook) {
    /*
    if (eppPlaybook.getCallsPerMinute()>120) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Calls per minute is maximum 120, because each command needs to wait for response from DRS. Increase numberOfThreads instead to generate more load.");
    }

     */
  }

  @PostMapping(value="/execute-epp")
  public String executeEPP(@RequestBody EppPlaybook eppPlaybook) {
    validateRequest(eppPlaybook);
    String response = "";
    EPPConnection eppConnection = new EPPConnection();
    eppConnection.connect(eppHost, eppPort);
    eppConnection.readWrite(eppCommands.getLogin(eppPlaybook.getUser(), eppPlaybook.getPassword()));

    List<String> commandList = Arrays.asList((eppPlaybook.getCommands().toUpperCase().split(",")));
    int repeatCount = eppPlaybook.getRepeatCount().intValue();
    DynamicValuesWrapper dynamicValues = new DynamicValuesWrapper();

    for (int i=0; i<repeatCount; i++) {
      CompletableFuture<String> cf = eppExecutorService.executeEpp(dynamicValues, commandList, eppPlaybook, eppConnection);
      int sleep_time =  (60 / eppPlaybook.getCallsPerMinute())  * 1000;
      try {
        Thread.sleep(sleep_time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      try {
        response = cf.get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    eppConnection.readWrite(eppCommands.getLogout());
    eppConnection.disConnect();

    return response;
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
        new EppPlaybook().toString();
  }

}
