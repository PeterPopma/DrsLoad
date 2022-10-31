package nl.sidn.drs.epplg.service;

import lombok.extern.slf4j.Slf4j;
import nl.sidn.drs.epplg.connection.EPPConnection;
import nl.sidn.drs.epplg.controller.DynamicValuesWrapper;
import nl.sidn.drs.epplg.controller.EppCommands;
import nl.sidn.drs.epplg.dto.EppPlaybook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EppExecutorService {
  EppCommands eppCommands = new EppCommands();

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

  @Async
  public CompletableFuture<String> executeEpp(DynamicValuesWrapper dynamicValues, List<String> commandList, EppPlaybook eppPlaybook, EPPConnection eppConnection) {
    String generatedContactHandle = "";
    String response = "";
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
          commandString = eppCommands.getDomainTransfer(eppPlaybook.getCommandParameters().getDomainName(), eppPlaybook.getCommandParameters().getDomainToken());
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

      // parse response to obtain handle
      if (command.equalsIgnoreCase("CONTACTCREATE")) {
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
