package com.peterpopma.drsload.service;
import com.peterpopma.drsload.dto.epp.Command;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class Scenario {
    private String name;
    private String initialLogin;
    private Integer repeatCount = 1;
    private Integer callsPerMinute = 30;
    private List<Command> commands;


    private Integer timesRun;

    private Instant timeLastRun;

    private Float runIntervalSeconds;
}
