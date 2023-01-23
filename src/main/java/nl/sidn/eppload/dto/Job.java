package nl.sidn.eppload.dto;

import lombok.Getter;
import lombok.Setter;
import nl.sidn.eppload.connection.EPPConnection;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class Job {
  String name;
  String hostName;
  Integer port;
  Integer runTimeSeconds;
  Integer doubleLoadSeconds;
  List<Scenario> scenarios;
  List<String> scenarioNames;

  Instant timeLastRun;
  EPPConnection eppConnection;
  Instant timeStarted;
  Instant timeLastDoubleLoad;

  int doubleLoadMultiplier;
}
