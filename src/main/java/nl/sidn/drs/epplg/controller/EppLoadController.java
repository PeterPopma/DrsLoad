package nl.sidn.drs.epplg.controller;

import lombok.extern.slf4j.Slf4j;
import nl.sidn.drs.epplg.connection.EPPConnection;
import nl.sidn.drs.epplg.dto.EppPlaybook;
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

@RestController
@Slf4j
public class EppLoadController {
  EppCommands eppCommands = new EppCommands();

  String eppHost = "localhost";
  int eppPort = 700;

  private String replacePlusPlusWithNumberValue(DynamicValuesWrapper dynamicValues, String text) {
    while(text.contains("++"))
    {
      int index_begin = text.indexOf("++") + 2;
      int index_end = index_begin;
      while(Character.isDigit(text.charAt(index_end))) {
        index_end++;
      }

      int translated_value;
      if (dynamicValues.getDynamicParameters().size()<=dynamicValues.getIndex()) {
        // new value
        translated_value = Integer.valueOf(text.substring(index_begin, index_end));
        dynamicValues.getDynamicParameters().add(translated_value);
      } else {
        // existing value
        Integer value = dynamicValues.getDynamicParameters().get(dynamicValues.getIndex());
        value++;
        dynamicValues.getDynamicParameters().set(dynamicValues.getIndex(), value);
        translated_value = value;
      }
      text = text.substring(0, index_begin-2) + translated_value + text.substring(index_end);

      dynamicValues.setIndex(dynamicValues.getIndex()+1);
    }
    return text;
  }

  private static String getFirstValueNotEmpty(String value1, String value2) {
    if (!value1.isEmpty()) {
      return value1;
    }

    return value2;
  }

  private void validateRequest(EppPlaybook eppPlaybook) {
    if (eppPlaybook.getCallsPerMinute()>120) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Calls per minute is maximum 120, because each command needs to wait for response from DRS. Increase numberOfThreads instead to generate more load.");
    }
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
      String generatedContactHandle = "";
      String generatedToken = "";
      dynamicValues.setIndex(0);
      for (String command : commandList) {
        String commandString = "";
        switch (command) {
          case "CONTACTCREATE":
            commandString = eppCommands.getContactCreate(eppPlaybook.getCommandParameters().getContactInfo().getName(),
                eppPlaybook.getCommandParameters().getContactInfo().getStreet(),
                eppPlaybook.getCommandParameters().getContactInfo().getCity(),
                eppPlaybook.getCommandParameters().getContactInfo().getPostalcode(),
                eppPlaybook.getCommandParameters().getContactInfo().getTelephone(),
                eppPlaybook.getCommandParameters().getContactInfo().getEmail());
            break;
          case "CONTACTUPDATE":
            commandString = eppCommands.getContactUpdate(eppPlaybook.getCommandParameters().getContactInfo().getName(),
                eppPlaybook.getCommandParameters().getContactInfo().getStreet(),
                eppPlaybook.getCommandParameters().getContactInfo().getCity(),
                eppPlaybook.getCommandParameters().getContactInfo().getPostalcode(),
                eppPlaybook.getCommandParameters().getContactInfo().getTelephone(),
                eppPlaybook.getCommandParameters().getContactInfo().getEmail());
            break;
          case "CONTACTINFO":
            commandString = eppConnection.readWrite(eppCommands.getContactInfo(getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getContactInfo().getHandle())));
            break;
          case "CONTACTDELETE":
            commandString = eppConnection.readWrite(eppCommands.getContactDelete(getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getContactInfo().getHandle())));

            break;
          case "DOMAINCREATE":
            commandString = eppCommands.getDomainCreate(
                eppPlaybook.getCommandParameters().getDomainName(),
                eppPlaybook.getCommandParameters().getHostName(),
                eppPlaybook.getCommandParameters().getHostName2(),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getRegistrant()),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getAdminC()),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getTechC()));
            break;
          case "DOMAININFO":
            commandString = eppCommands.getDomainInfo(eppPlaybook.getCommandParameters().getDomainName());
            break;
          case "DOMAINUPDATE":
            commandString = eppCommands.getDomainUpdate(
                eppPlaybook.getCommandParameters().getDomainName(),
                eppPlaybook.getCommandParameters().getHostName(),
                eppPlaybook.getCommandParameters().getHostName2(),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getRegistrant()),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getAdminC()),
                getFirstValueNotEmpty(generatedContactHandle, eppPlaybook.getCommandParameters().getTechC()));
            break;
          case "DOMAINDELETE":
            commandString = eppCommands.getDomainDelete(eppPlaybook.getCommandParameters().getDomainName());
            break;
          case "DOMAINTRANSFER":
            commandString = eppCommands.getDomainTransfer(eppPlaybook.getCommandParameters().getDomainName(), getFirstValueNotEmpty(generatedToken, eppPlaybook.getCommandParameters().getDomainToken()));
            break;
          case "DOMAINRENEW":
            commandString = eppCommands.getDomainRenew(eppPlaybook.getCommandParameters().getDomainName(), eppPlaybook.getCommandParameters().getRenewPeriod());
            break;
          case "HOSTCREATE":
            commandString = eppCommands.getHostCreate(eppPlaybook.getCommandParameters().getHostName(), eppPlaybook.getCommandParameters().getIpAddress());
            break;
          case "HOSTINFO":
            commandString = eppCommands.getHostInfo(eppPlaybook.getCommandParameters().getHostName());
            break;
          case "HOSTUPDATE":
            commandString = eppCommands.getHostUpdate(eppPlaybook.getCommandParameters().getHostName(), eppPlaybook.getCommandParameters().getIpAddressAdd(), eppPlaybook.getCommandParameters().getIpAddressRemove(), eppPlaybook.getCommandParameters().getHostNameNew());
            break;
          case "HOSTDELETE":
            commandString = eppCommands.getHostDelete(eppPlaybook.getCommandParameters().getHostName());
            break;
          case "POLL":
            commandString = eppCommands.getPoll();
            break;
        }
        commandString = replacePlusPlusWithNumberValue(dynamicValues, commandString);
        response = eppConnection.readWrite(commandString);

        // parse response to obtain token
        if(command.equalsIgnoreCase("DOMAINCREATE"))
        {
          generatedToken = extractHandle(response);
        }
        // parse response to obtain handle
        if(command.equalsIgnoreCase("CONTACTCREATE"))
        {
          generatedContactHandle = extractHandle(response);
        }

        log.debug(response);
      }
      int sleep_time =  (60 / eppPlaybook.getCallsPerMinute())  * 1000;
      try {
        Thread.sleep(sleep_time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    eppConnection.readWrite(eppCommands.getLogout());
    eppConnection.disConnect();

    return response;
  }

  public static String getTagValue(String xml, String tagName) {
    String value = "";
    try {
      value = xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
    } catch (ArrayIndexOutOfBoundsException e) {
      log.debug("could not parse XML response.", e);
    }
    return value;
  }

  private String extractHandle(String response) {
    return getTagValue(response, "contact:id");
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
