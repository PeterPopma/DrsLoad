package nl.sidn.eppload.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.sidn.eppload.connection.EPPConnection;
import nl.sidn.eppload.controller.DynamicValuesWrapper;
import nl.sidn.eppload.controller.EppCommands;
import nl.sidn.eppload.dto.Command;
import nl.sidn.eppload.dto.Scenario;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EppExecutorService {
  EppCommands eppCommands = new EppCommands();
  Random rand = new Random();

  private MeterRegistry meterRegistry;
  private Counter counterCONTACTCREATE, counterCONTACTUPDATE, counterCONTACTINFO, counterCONTACTDELETE;
  private Counter counterDOMAINCREATE, counterDOMAINUPDATE, counterDOMAININFO, counterDOMAINDELETE, counterDOMAINTRANSFER, counterDOMAINTRANSFERQUERY, counterDOMAINRENEW;
  private Counter counterHOSTCREATE, counterHOSTUPDATE, counterHOSTINFO, counterHOSTDELETE, counterPOLL, counterEPPRequests;

  public EppExecutorService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    counterCONTACTCREATE = meterRegistry.counter("contactcreate_count");
    counterCONTACTUPDATE = meterRegistry.counter("contactupdate_count");
    counterCONTACTINFO = meterRegistry.counter("contactinfo_count");
    counterCONTACTDELETE = meterRegistry.counter("contactdelete_count");
    counterDOMAINCREATE = meterRegistry.counter("domaincreate_count");
    counterDOMAINUPDATE = meterRegistry.counter("domainupdate_count");
    counterDOMAININFO = meterRegistry.counter("domaininfo_count");
    counterDOMAINDELETE = meterRegistry.counter("domaindelete_count");
    counterDOMAINTRANSFER = meterRegistry.counter("domaintransfer_count");
    counterDOMAINTRANSFERQUERY = meterRegistry.counter("domaintransferquery_count");
    counterDOMAINRENEW = meterRegistry.counter("domainrenew_count");
    counterHOSTCREATE = meterRegistry.counter("hostcreate_count");
    counterHOSTUPDATE = meterRegistry.counter("hostupdate_count");
    counterHOSTINFO = meterRegistry.counter("hostinfo_count");
    counterHOSTDELETE = meterRegistry.counter("hostdelete_count");
    counterPOLL = meterRegistry.counter("poll_count");
    counterEPPRequests = meterRegistry.counter("epp_requests_count");
  }

  private static String getFirstValueNotEmpty(String value1, String value2) {
    if (!value1.isEmpty()) {
      return value1;
    }

    return value2;
  }

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

  private String replaceQuestionMarkQuestionMarkWitRandomValue(String text) {
    while(text.contains("??"))
    {
      int index_begin = text.indexOf("??") + 2;
      int index_end = index_begin;
      while(Character.isDigit(text.charAt(index_end))) {
        index_end++;
      }

      int random_value = rand.nextInt(Integer.valueOf(text.substring(index_begin, index_end)));

      text = text.substring(0, index_begin-2) + random_value + text.substring(index_end);
    }
    return text;
  }

  @Async
  public CompletableFuture<String> executeScenario(DynamicValuesWrapper dynamicValues, Scenario scenario, EPPConnection eppConnection) {
    String createdContactHandle = "";
    String createdDomainName = "";
    String domainToken = "";
    String createdHostName = "";
    String response = "";
    dynamicValues.setIndex(0);

    for (Command command : scenario.getCommands()) {
      String commandString = "";
      switch (command.getCommand()) {
        case "CONTACTCREATE":
          counterCONTACTCREATE.increment();
          commandString = eppCommands.getContactCreate(command.getParameters().getContactInfo().getName(),
              command.getParameters().getContactInfo().getStreet(),
              command.getParameters().getContactInfo().getCity(),
              command.getParameters().getContactInfo().getPostalcode(),
              command.getParameters().getContactInfo().getTelephone(),
              command.getParameters().getContactInfo().getEmail());
          break;
        case "CONTACTUPDATE":
          counterCONTACTUPDATE.increment();
          commandString = eppCommands.getContactUpdate(getFirstValueNotEmpty(command.getParameters().getContactInfo().getName(), createdContactHandle),
              command.getParameters().getContactInfo().getStreet(),
              command.getParameters().getContactInfo().getCity(),
              command.getParameters().getContactInfo().getPostalcode(),
              command.getParameters().getContactInfo().getTelephone(),
              command.getParameters().getContactInfo().getEmail());
          break;
        case "CONTACTINFO":
          counterCONTACTINFO.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactInfo(getFirstValueNotEmpty(command.getParameters().getContactInfo().getHandle(), createdContactHandle)));
          break;
        case "CONTACTDELETE":
          counterCONTACTDELETE.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactDelete(getFirstValueNotEmpty(command.getParameters().getContactInfo().getHandle(), createdContactHandle)));
          break;
        case "DOMAINCREATE":
          counterDOMAINCREATE.increment();
          createdDomainName = command.getParameters().getDomainName();
          commandString = eppCommands.getDomainCreate(
              command.getParameters().getDomainName(),
              command.getParameters().getHostName(),
              command.getParameters().getHostName2(),
              getFirstValueNotEmpty(command.getParameters().getRegistrant(), createdContactHandle),
              getFirstValueNotEmpty(command.getParameters().getAdminC(), createdContactHandle),
              getFirstValueNotEmpty(command.getParameters().getTechC(), createdContactHandle));
          break;
        case "DOMAININFO":
          counterDOMAININFO.increment();
          commandString = eppCommands.getDomainInfo(getFirstValueNotEmpty(command.getParameters().getDomainName(), createdDomainName));
          break;
        case "DOMAINUPDATE":
          counterDOMAINUPDATE.increment();
          commandString = eppCommands.getDomainUpdate(
              command.getParameters().getDomainName(),
              command.getParameters().getHostName(),
              command.getParameters().getHostName2(),
              getFirstValueNotEmpty(command.getParameters().getRegistrant(), createdContactHandle),
              getFirstValueNotEmpty(command.getParameters().getAdminC(), createdContactHandle),
              getFirstValueNotEmpty(command.getParameters().getTechC(), createdContactHandle));
          break;
        case "DOMAINDELETE":
          counterDOMAINDELETE.increment();
          commandString = eppCommands.getDomainDelete(getFirstValueNotEmpty(command.getParameters().getDomainName(), createdDomainName));
          break;
        case "DOMAINTRANSFER":
          counterDOMAINTRANSFER.increment();
          commandString = eppCommands.getDomainTransfer(getFirstValueNotEmpty(command.getParameters().getDomainName(), createdDomainName), domainToken);
          break;
        case "DOMAINTRANSFERQUERY":
          counterDOMAINTRANSFERQUERY.increment();
          commandString = eppCommands.getDomainTransferQuery(getFirstValueNotEmpty(command.getParameters().getDomainName(), createdDomainName));
          break;
        case "DOMAINRENEW":
          counterDOMAINRENEW.increment();
          commandString = eppCommands.getDomainRenew(getFirstValueNotEmpty(command.getParameters().getDomainName(), createdDomainName), command.getParameters().getRenewPeriod());
          break;
        case "HOSTCREATE":
          counterHOSTCREATE.increment();
          createdHostName = command.getParameters().getHostName();
          commandString = eppCommands.getHostCreate(command.getParameters().getHostName(), command.getParameters().getIpAddress());
          break;
        case "HOSTINFO":
          counterHOSTINFO.increment();
          commandString = eppCommands.getHostInfo(getFirstValueNotEmpty(command.getParameters().getHostName(), createdHostName));
          break;
        case "HOSTUPDATE":
          counterHOSTUPDATE.increment();
          commandString = eppCommands.getHostUpdate(getFirstValueNotEmpty(command.getParameters().getHostName(), createdHostName), command.getParameters().getIpAddressAdd(), command.getParameters().getIpAddressRemove(), command.getParameters().getHostNameNew());
          break;
        case "HOSTDELETE":
          counterHOSTDELETE.increment();
          commandString = eppCommands.getHostDelete(getFirstValueNotEmpty(command.getParameters().getHostName(), createdHostName));
          break;
        case "POLL":
          counterPOLL.increment();
          commandString = eppCommands.getPoll();
          break;
      }
      commandString = replacePlusPlusWithNumberValue(dynamicValues, commandString);
      commandString = replaceQuestionMarkQuestionMarkWitRandomValue(commandString);
      counterEPPRequests.increment();
      response = eppConnection.readWrite(commandString);
      if (response.equals("error")) {
        log.error("epp I/O error!");
      }

      // parse response to obtain handle
      if (command.getCommand().equalsIgnoreCase("CONTACTCREATE")) {
        createdContactHandle = extractHandle(response);
      }

      // parse response to obtain token
      if (command.getCommand().equalsIgnoreCase("DOMAININFO")) {
        domainToken = extractToken(response);
      }

      log.debug(response);
    }


    return CompletableFuture.completedFuture(response);
  }

  private static String getTagValue(String xml, String tagName) {
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

  private String extractToken(String response) {
    return getTagValue(response, "domain:pw");
  }
}
