package nl.sidn.eppload.service;

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
@RequiredArgsConstructor
public class EppExecutorService {
  EppCommands eppCommands = new EppCommands();
  Random rand = new Random();

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
    String generatedContactHandle = "";
    String response = "";
    dynamicValues.setIndex(0);

    for (Command command : scenario.getCommands()) {
      String commandString = "";
      switch (command.getCommand()) {
        case "CONTACTCREATE":
          commandString = eppCommands.getContactCreate(command.getParameters().getContactInfo().getName(),
              command.getParameters().getContactInfo().getStreet(),
              command.getParameters().getContactInfo().getCity(),
              command.getParameters().getContactInfo().getPostalcode(),
              command.getParameters().getContactInfo().getTelephone(),
              command.getParameters().getContactInfo().getEmail());
          break;
        case "CONTACTUPDATE":
          commandString = eppCommands.getContactUpdate(command.getParameters().getContactInfo().getName(),
              command.getParameters().getContactInfo().getStreet(),
              command.getParameters().getContactInfo().getCity(),
              command.getParameters().getContactInfo().getPostalcode(),
              command.getParameters().getContactInfo().getTelephone(),
              command.getParameters().getContactInfo().getEmail());
          break;
        case "CONTACTINFO":
          commandString = eppConnection.readWrite(eppCommands.getContactInfo(getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getContactInfo().getHandle())));
          break;
        case "CONTACTDELETE":
          commandString = eppConnection.readWrite(eppCommands.getContactDelete(getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getContactInfo().getHandle())));

          break;
        case "DOMAINCREATE":
          commandString = eppCommands.getDomainCreate(
              command.getParameters().getDomainName(),
              command.getParameters().getHostName(),
              command.getParameters().getHostName2(),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getRegistrant()),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getAdminC()),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getTechC()));
          break;
        case "DOMAININFO":
          commandString = eppCommands.getDomainInfo(command.getParameters().getDomainName());
          break;
        case "DOMAINUPDATE":
          commandString = eppCommands.getDomainUpdate(
              command.getParameters().getDomainName(),
              command.getParameters().getHostName(),
              command.getParameters().getHostName2(),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getRegistrant()),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getAdminC()),
              getFirstValueNotEmpty(generatedContactHandle, command.getParameters().getTechC()));
          break;
        case "DOMAINDELETE":
          commandString = eppCommands.getDomainDelete(command.getParameters().getDomainName());
          break;
        case "DOMAINTRANSFER":
          commandString = eppCommands.getDomainTransfer(command.getParameters().getDomainName(), command.getParameters().getDomainToken());
          break;
        case "DOMAINRENEW":
          commandString = eppCommands.getDomainRenew(command.getParameters().getDomainName(), command.getParameters().getRenewPeriod());
          break;
        case "HOSTCREATE":
          commandString = eppCommands.getHostCreate(command.getParameters().getHostName(), command.getParameters().getIpAddress());
          break;
        case "HOSTINFO":
          commandString = eppCommands.getHostInfo(command.getParameters().getHostName());
          break;
        case "HOSTUPDATE":
          commandString = eppCommands.getHostUpdate(command.getParameters().getHostName(), command.getParameters().getIpAddressAdd(), command.getParameters().getIpAddressRemove(), command.getParameters().getHostNameNew());
          break;
        case "HOSTDELETE":
          commandString = eppCommands.getHostDelete(command.getParameters().getHostName());
          break;
        case "POLL":
          commandString = eppCommands.getPoll();
          break;
      }
      commandString = replacePlusPlusWithNumberValue(dynamicValues, commandString);
      commandString = replaceQuestionMarkQuestionMarkWitRandomValue(commandString);
      response = eppConnection.readWrite(commandString);

      // parse response to obtain handle
      if (command.getCommand().equalsIgnoreCase("CONTACTCREATE")) {
        generatedContactHandle = extractHandle(response);
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
}
