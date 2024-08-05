package com.peterpopma.drsload.service;

import com.peterpopma.drsload.connection.EPPConnection;
import com.peterpopma.drsload.controller.DynamicValuesWrapper;
import com.peterpopma.drsload.controller.EppCommands;
import com.peterpopma.drsload.dto.epp.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.peterpopma.drsload.dto.epp.EppCommand.CONTACTCREATE;
import static com.peterpopma.drsload.dto.epp.EppCommand.DOMAININFO;
import static org.springframework.http.MediaType.APPLICATION_JSON;

enum EppObjectType {
  CONTACT,
  DOMAIN,
  HOST
}

@Slf4j
@Service
public class CommandExecutorService {
  EppCommands eppCommands = new EppCommands();

  private final RestClient restClient;

  private MeterRegistry meterRegistry;
  private Counter counterCONTACTCREATE;
  private Counter counterCONTACTUPDATE;
  private Counter counterCONTACTINFO;
  private Counter counterCONTACTDELETE;
  private Counter counterDOMAINCREATE;
  private Counter counterDOMAINUPDATE;
  private Counter counterDOMAININFO;
  private Counter counterDOMAINDELETE;
  private Counter counterDOMAINTRANSFER;
  private Counter counterDOMAINTRANSFERQUERY;
  private Counter counterDOMAINRENEW;
  private Counter counterHOSTCREATE;
  private Counter counterHOSTUPDATE;
  private Counter counterHOSTINFO;
  private Counter counterHOSTDELETE;
  private Counter counterPOLL;
  private Counter counterRequests;


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
    counterRequests = meterRegistry.counter("requests_count");

    restClient = RestClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
  }


  @Async
  public CompletableFuture<String> executeScenarioEPP(DynamicValuesWrapper dynamicValues, Scenario scenario, EPPConnection eppConnection) {
    String createdContactHandle = "";
    String createdDomainName = "";
    String domainToken = "";
    String createdHostName = "";
    String response = "";
    dynamicValues.setIndex(0);

    for (Command command : scenario.getCommands()) {
      String commandString = "";
      switch (command.commandType()) {
        case CONTACTCREATE:
          counterCONTACTCREATE.increment();
          commandString = eppCommands.getContactCreate(command.contactObject().name(),
              command.contactObject().postalInfo().get(0).addr().street().toString(),
              command.contactObject().postalInfo().get(0).addr().city(),
              command.contactObject().postalInfo().get(0).addr().pc(),
              command.contactObject().voice(),
              command.contactObject().email());
          break;
        case CONTACTUPDATE:
          counterCONTACTUPDATE.increment();
          commandString = eppCommands.getContactUpdate(CommandUtils.getFirstValueNotEmpty(command.contactObject().name(), createdContactHandle),
              command.contactObject().postalInfo().get(0).addr().street().toString(),
              command.contactObject().postalInfo().get(0).addr().city(),
              command.contactObject().postalInfo().get(0).addr().pc(),
              command.contactObject().voice(),
              command.contactObject().email());
          break;
        case CONTACTINFO:
          counterCONTACTINFO.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactInfo(CommandUtils.getFirstValueNotEmpty(command.contactObject().id(), createdContactHandle)));
          break;
        case CONTACTDELETE:
          counterCONTACTDELETE.increment();
          commandString = eppConnection.readWrite(eppCommands.getContactDelete(CommandUtils.getFirstValueNotEmpty(command.contactObject().id(), createdContactHandle)));
          break;
        case DOMAINCREATE:
          counterDOMAINCREATE.increment();
          createdDomainName = command.domainObject().id();
          commandString = eppCommands.getDomainCreate(
              command.domainObject().id(),
              command.domainObject().hosts(),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().registrant(), createdContactHandle),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().adminC(), createdContactHandle),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().techC(), createdContactHandle));
          break;
        case DOMAININFO:
          counterDOMAININFO.increment();
          commandString = eppCommands.getDomainInfo(CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName));
          break;
        case DOMAINUPDATE:
          counterDOMAINUPDATE.increment();
          commandString = eppCommands.getDomainUpdate(
              command.domainObject().id(),
              command.domainObject().hosts(),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().registrant(), createdContactHandle),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().adminC(), createdContactHandle),
                  CommandUtils.getFirstValueNotEmpty(command.domainObject().techC(), createdContactHandle));
          break;
        case DOMAINDELETE:
          counterDOMAINDELETE.increment();
          commandString = eppCommands.getDomainDelete(CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName));
          break;
        case DOMAINTRANSFER:
          counterDOMAINTRANSFER.increment();
          commandString = eppCommands.getDomainTransfer(CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName), domainToken);
          break;
        case DOMAINTRANSFERQUERY:
          counterDOMAINTRANSFERQUERY.increment();
          commandString = eppCommands.getDomainTransferQuery(CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName));
          break;
        case DOMAINRENEW:
          counterDOMAINRENEW.increment();
          commandString = eppCommands.getDomainRenew(CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName), command.domainObject().period());
          break;
        case HOSTCREATE:
          counterHOSTCREATE.increment();
          createdHostName = command.hostObject().name();
          commandString = eppCommands.getHostCreate(command.hostObject().name(), command.hostObject().addr());
          break;
        case HOSTINFO:
          counterHOSTINFO.increment();
          commandString = eppCommands.getHostInfo(CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName));
          break;
        case HOSTUPDATE:
          counterHOSTUPDATE.increment();
          commandString = eppCommands.getHostUpdate(CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName), command.hostObject().addr(), command.hostObject().newName());
          break;
        case HOSTDELETE:
          counterHOSTDELETE.increment();
          commandString = eppCommands.getHostDelete(CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName));
          break;
        case POLL:
          counterPOLL.increment();
          commandString = eppCommands.getPoll();
          break;
        default:
          log.error("encountered unknown command");
          break;
      }
      commandString = CommandUtils.replacePlusPlusWithNumberValue(dynamicValues, commandString);
      commandString = CommandUtils.replaceQuestionMarkQuestionMarkWitRandomValue(commandString);
      counterRequests.increment();
      response = eppConnection.readWrite(commandString);
      if (response.equals("error")) {
        log.error("epp I/O error!");
      }

      // parse response to obtain handle
      if (command.commandType()==CONTACTCREATE) {
        createdContactHandle = CommandUtils.extractHandle(response);
      }

      // parse response to obtain token
      if (command.commandType()==DOMAININFO) {
        domainToken = CommandUtils.extractToken(response);
      }

      log.debug(response);
    }

    return CompletableFuture.completedFuture(response);
  }

  @Async
  public CompletableFuture<Response> executeScenarioREST(DynamicValuesWrapper dynamicValues, Scenario scenario) {
    Response response = null;
    String createdContactHandle = "";
    String createdDomainName = "";
    String createdHostName = "";
    String domainToken = "";

    dynamicValues.setIndex(0);

    for (Command command : scenario.getCommands()) {
      counterRequests.increment();
      switch (command.commandType()) {
        case CONTACTCREATE:
          counterCONTACTCREATE.increment();
          /*
          TODO: Create a JSON string directly and pass it in the body. That way we can replace all dynamic values in the string.

          commandString = eppCommands.getContactCreateJSON(command.contactObject().name(),
                  command.contactObject().postalInfo().get(0).addr().street().toString(),
                  command.contactObject().postalInfo().get(0).addr().city(),
                  command.contactObject().postalInfo().get(0).addr().pc(),
                  command.contactObject().voice(),
                  command.contactObject().email());
          commandString = CommandUtils.replacePlusPlusWithNumberValue(dynamicValues, commandString);
          commandString = CommandUtils.replaceQuestionMarkQuestionMarkWitRandomValue(commandString);
           */

          ContactAddress addr = new ContactAddress(command.contactObject().postalInfo().get(0).addr().street(),
                  command.contactObject().postalInfo().get(0).addr().city(),
                  command.contactObject().postalInfo().get(0).addr().sp(),
                  command.contactObject().postalInfo().get(0).addr().pc(),
                  command.contactObject().postalInfo().get(0).addr().cc());
          ContactPostalInfo contactPostalInfo = new ContactPostalInfo(command.contactObject().name(), null, addr);
          ContactObject contactObject = new ContactObject(null, command.contactObject().roid(),
                  command.contactObject().name(), command.contactObject().status(), Collections.singletonList(contactPostalInfo), command.contactObject().voice(), command.contactObject().fax(), command.contactObject().email(), command.contactObject().clID(),
                  command.contactObject().crID(), command.contactObject().crDate(), command.contactObject().upID(), command.contactObject().upDate(), command.contactObject().trDate(), command.contactObject().authInfo(), command.contactObject().disclose());
          response = restClient.post().uri("/" + EppObjectType.CONTACT)
                  .contentType(APPLICATION_JSON)
                  .body(contactObject)
                  .retrieve().body(Response.class);

          createdContactHandle = response.resData().contactObject().id();
          break;
        case CONTACTUPDATE:
          counterCONTACTUPDATE.increment();
          String contactId = CommandUtils.getFirstValueNotEmpty(command.contactObject().id(), createdContactHandle);
          addr = new ContactAddress(command.contactObject().postalInfo().get(0).addr().street(),
                  command.contactObject().postalInfo().get(0).addr().city(),
                  command.contactObject().postalInfo().get(0).addr().sp(),
                  command.contactObject().postalInfo().get(0).addr().pc(),
                  command.contactObject().postalInfo().get(0).addr().cc());
          contactPostalInfo = new ContactPostalInfo(command.contactObject().name(), null, addr);
          contactObject = new ContactObject(contactId, command.contactObject().roid(),
                  command.contactObject().name(), command.contactObject().status(), Collections.singletonList(contactPostalInfo), command.contactObject().voice(), command.contactObject().fax(), command.contactObject().email(), command.contactObject().clID(),
                  command.contactObject().crID(), command.contactObject().crDate(), command.contactObject().upID(), command.contactObject().upDate(), command.contactObject().trDate(), command.contactObject().authInfo(), command.contactObject().disclose());

          response = restClient.put().uri("/" + EppObjectType.CONTACT)
                  .contentType(APPLICATION_JSON)
                  .body(contactObject)
                  .retrieve().body(Response.class);
          break;
        case CONTACTINFO:
          counterCONTACTINFO.increment();
          contactId = CommandUtils.getFirstValueNotEmpty(command.contactObject().id(), createdContactHandle);
          response = restClient.get().uri("/" + EppObjectType.CONTACT + "/" + contactId)
                  .retrieve().body(Response.class);
          break;
        case CONTACTDELETE:
          counterCONTACTDELETE.increment();
          contactId = CommandUtils.getFirstValueNotEmpty(command.contactObject().id(), createdContactHandle);
          response = restClient.delete().uri("/" + EppObjectType.CONTACT + "/" + contactId)
                  .retrieve().body(Response.class);
          break;
        case DOMAINCREATE:
          counterDOMAINCREATE.increment();
          DomainObject domainObject = getDomainObject(command, null, null);
          response = restClient.post().uri("/" + EppObjectType.DOMAIN)
                  .contentType(APPLICATION_JSON)
                  .body(domainObject)
                  .retrieve().body(Response.class);
          createdDomainName = response.resData().domainObject().id();
          break;
        case DOMAINUPDATE:
          counterDOMAINUPDATE.increment();
          String domainId = CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName);
          domainObject = getDomainObject(command, domainId, null);
          response = restClient.put().uri("/" + EppObjectType.DOMAIN)
                  .contentType(APPLICATION_JSON)
                  .body(domainObject)
                  .retrieve().body(Response.class);
          break;
        case DOMAININFO:
          counterDOMAININFO.increment();
          domainId = CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName);
          response = restClient.get().uri("/" + EppObjectType.DOMAIN + "/" + domainId)
                  .retrieve().body(Response.class);
          // obtain token
          domainToken = response.resData().domainObject().authInfo().pw();
          break;
        case DOMAINDELETE:
          counterDOMAINDELETE.increment();
          domainId = CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName);
          response = restClient.delete().uri("/" + EppObjectType.DOMAIN + "/" + domainId)
                  .retrieve().body(Response.class);
          break;
        case DOMAINTRANSFER:
          counterDOMAINTRANSFER.increment();
          domainId = CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName);
          String token =  CommandUtils.getFirstValueNotEmpty(command.domainObject().authInfo().pw(), domainToken);
          domainObject = getDomainObject(command, domainId, token);
          response = restClient.post().uri("/" + EppObjectType.DOMAIN + "/transfer")
                  .contentType(APPLICATION_JSON)
                  .body(domainObject)
                  .retrieve().body(Response.class);
          break;
        case DOMAINRENEW:
          counterDOMAINRENEW.increment();
          domainId = CommandUtils.getFirstValueNotEmpty(command.domainObject().id(), createdDomainName);
          domainObject = getDomainObject(command, domainId, null);
          response = restClient.post().uri("/" + EppObjectType.DOMAIN + "/renew")
                  .contentType(APPLICATION_JSON)
                  .body(domainObject)
                  .retrieve().body(Response.class);
          break;
        case HOSTCREATE:
          counterHOSTCREATE.increment();
          HostObject hostObject = new HostObject(command.hostObject().roid(), command.hostObject().name(), command.hostObject().newName(), command.hostObject().status(), command.hostObject().addr(),
                  command.hostObject().clID(), command.hostObject().crID(), command.hostObject().crDate(), command.hostObject().upID(),
                  command.hostObject().upDate(),command.hostObject().trDate());
          response = restClient.post().uri("/" + EppObjectType.HOST)
                  .contentType(APPLICATION_JSON)
                  .body(hostObject)
                  .retrieve().body(Response.class);
          createdHostName = response.resData().hostObject().name();
          break;
        case HOSTUPDATE:
          counterHOSTUPDATE.increment();
          String hostName = CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName);
          hostObject = new HostObject(command.hostObject().roid(), hostName, command.hostObject().newName(), command.hostObject().status(), command.hostObject().addr(),
                  command.hostObject().clID(), command.hostObject().crID(), command.hostObject().crDate(), command.hostObject().upID(),
                  command.hostObject().upDate(),command.hostObject().trDate());
          response = restClient.put().uri("/" + EppObjectType.HOST)
                  .contentType(APPLICATION_JSON)
                  .body(hostObject)
                  .retrieve().body(Response.class);
          break;
        case HOSTINFO:
          counterHOSTINFO.increment();
          hostName = CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName);
          response = restClient.get().uri("/" + EppObjectType.HOST + "/" + hostName)
                  .retrieve().body(Response.class);
          break;
        case HOSTDELETE:
          counterHOSTDELETE.increment();
          hostName = CommandUtils.getFirstValueNotEmpty(command.hostObject().name(), createdHostName);
          response = restClient.delete().uri("/" + EppObjectType.HOST + "/" + hostName)
                  .retrieve().body(Response.class);
          break;
        case POLL:
          counterPOLL.increment();
          response = restClient.get().uri("/poll")
                  .retrieve().body(Response.class);
          break;
        default:
          log.error("encountered unknown command");
          break;
      }

      log.debug(response.toString());
    }

    return CompletableFuture.completedFuture(response);
  }

  private static DomainObject getDomainObject(Command command, String domainId, String pw) {
    PeriodObject periodObject = new PeriodObject(Unit.Y, 1);
    return new DomainObject(domainId, command.domainObject().roid(), periodObject, command.domainObject().status(),
            command.domainObject().registrant(), command.domainObject().adminC(), command.domainObject().techC(),
            command.domainObject().hosts(), command.domainObject().clID(), command.domainObject().crID(), command.domainObject().crDate(),
            command.domainObject().upID(), command.domainObject().upDate(), command.domainObject().exDate(), command.domainObject().trDate(), new DomainAuthInfo(pw, null));
  }

}
