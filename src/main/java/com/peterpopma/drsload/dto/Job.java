package com.peterpopma.drsload.dto;

import com.peterpopma.drsload.connection.EPPConnection;
import lombok.Getter;
import lombok.Setter;

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

  Integer doubleLoadMultiplier;

  Boolean eppMode;
}
