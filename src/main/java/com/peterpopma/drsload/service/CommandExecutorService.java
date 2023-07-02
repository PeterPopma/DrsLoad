package com.peterpopma.drsload.service;

import com.peterpopma.drsload.connection.EPPConnection;
import com.peterpopma.drsload.controller.DynamicValuesWrapper;
import com.peterpopma.drsload.controller.EppCommands;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import com.peterpopma.drsload.dto.Command;
import com.peterpopma.drsload.dto.Scenario;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CommandExecutorService {
  EppCommands eppCommands = new EppCommands();
  Random rand = new Random();

  private MeterRegistry meterRegistry;
  private Counter counterCONTACTCREATE, counterCONTACTUPDATE, counterCONTACTINFO, counterCONTACTDELETE;
  private Counter counterDOMAINCREATE, counterDOMAINUPDATE, counterDOMAININFO, counterDOMAINDELETE, counterDOMAINTRANSFER, counterDOMAINTRANSFERQUERY, counterDOMAINRENEW;
  private Counter counterHOSTCREATE, counterHOSTUPDATE, counterHOSTINFO, counterHOSTDELETE, counterPOLL, counterEPPRequests;

  public CommandExecutorService(MeterRegistry meterRegistry) {
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
    if (value1!=null && !value1.isEmpty()) {
      return value1;
    }

    return value2;
  }

  private static List<String> getFirstValueNotEmpty(List<String> value1, String value2) {
    if (value1!=null && !value1.isEmpty()) {
      return value1;
    }

    List<String> value2List = new ArrayList<>();
    value2List.add(value2);
    return value2List;
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
      switch (command.getCommandType()) {
        case "CONTACTCREATE":
          counterCONTACTCREATE.increment();
          commandString = eppCommands.getContactCreate(command.getContactObject().getName(),
              command.getContactObject().getPostalInfo().get(0).getAddr().getStreet().toString(),
              command.getContactObject().getPostalInfo().get(0).getAddr().getCity(),
              command.getContactObject().getPostalInfo().get(0).getAddr().getPc(),
              command.getContactObject().getVoice(),
              command.getContactObject().getEmail());
          break;
        case "CONTACTUPDATE":
          counterCONTACTUPDATE.increment();
          commandString = eppCommands.getContactUpdate(getFirstValueNotEmpty(command.getContactObject().getName(), createdContactHandle),
              command.getContactObject().getPostalInfo().get(0).getAddr().getStreet().toString(),
              command.getContactObject().getPostalInfo().get(0).getAddr().getCity(),
              command.getContactObject().getPostalInfo().get(0).getAddr().getPc(),
              command.getContactObject().getVoice(),
              command.getContactObject().getEmail());
          break;
        case "CONTACTINFO":
          counterCONTACTINFO.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactInfo(getFirstValueNotEmpty(command.getContactObject().getId(), createdContactHandle)));
          break;
        case "CONTACTDELETE":
          counterCONTACTDELETE.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactDelete(getFirstValueNotEmpty(command.getContactObject().getId(), createdContactHandle)));
          break;
        case "DOMAINCREATE":
          counterDOMAINCREATE.increment();
          createdDomainName = command.getDomainObject().getId();
          commandString = eppCommands.getDomainCreate(
              command.getDomainObject().getId(),
              command.getDomainObject().getHosts(),
              getFirstValueNotEmpty(command.getDomainObject().getRegistrant(), createdContactHandle),
              getFirstValueNotEmpty(command.getDomainObject().getAdminC(), createdContactHandle),
              getFirstValueNotEmpty(command.getDomainObject().getTechC(), createdContactHandle));
          break;
        case "DOMAININFO":
          counterDOMAININFO.increment();
          commandString = eppCommands.getDomainInfo(getFirstValueNotEmpty(command.getDomainObject().getId(), createdDomainName));
          break;
        case "DOMAINUPDATE":
          counterDOMAINUPDATE.increment();
          commandString = eppCommands.getDomainUpdate(
              command.getDomainObject().getId(),
              command.getDomainObject().getHosts(),
              getFirstValueNotEmpty(command.getDomainObject().getRegistrant(), createdContactHandle),
              getFirstValueNotEmpty(command.getDomainObject().getAdminC(), createdContactHandle),
              getFirstValueNotEmpty(command.getDomainObject().getTechC(), createdContactHandle));
          break;
        case "DOMAINDELETE":
          counterDOMAINDELETE.increment();
          commandString = eppCommands.getDomainDelete(getFirstValueNotEmpty(command.getDomainObject().getId(), createdDomainName));
          break;
        case "DOMAINTRANSFER":
          counterDOMAINTRANSFER.increment();
          commandString = eppCommands.getDomainTransfer(getFirstValueNotEmpty(command.getDomainObject().getId(), createdDomainName), domainToken);
          break;
        case "DOMAINTRANSFERQUERY":
          counterDOMAINTRANSFERQUERY.increment();
          commandString = eppCommands.getDomainTransferQuery(getFirstValueNotEmpty(command.getDomainObject().getId(), createdDomainName));
          break;
        case "DOMAINRENEW":
          counterDOMAINRENEW.increment();
          commandString = eppCommands.getDomainRenew(getFirstValueNotEmpty(command.getDomainObject().getId(), createdDomainName), command.getDomainObject().getPeriod());
          break;
        case "HOSTCREATE":
          counterHOSTCREATE.increment();
          createdHostName = command.getHostObject().getName();
          commandString = eppCommands.getHostCreate(command.getHostObject().getName(), command.getHostObject().getAddr());
          break;
        case "HOSTINFO":
          counterHOSTINFO.increment();
          commandString = eppCommands.getHostInfo(getFirstValueNotEmpty(command.getHostObject().getName(), createdHostName));
          break;
        case "HOSTUPDATE":
          counterHOSTUPDATE.increment();
          commandString = eppCommands.getHostUpdate(getFirstValueNotEmpty(command.getHostObject().getName(), createdHostName), command.getHostObject().getAddr(), command.getHostObject().getNewName());
          break;
        case "HOSTDELETE":
          counterHOSTDELETE.increment();
          commandString = eppCommands.getHostDelete(getFirstValueNotEmpty(command.getHostObject().getName(), createdHostName));
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
      if (command.getCommandType().equalsIgnoreCase("CONTACTCREATE")) {
        createdContactHandle = extractHandle(response);
      }

      // parse response to obtain token
      if (command.getCommandType().equalsIgnoreCase("DOMAININFO")) {
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
