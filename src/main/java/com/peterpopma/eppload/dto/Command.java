package com.peterpopma.eppload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
  String command;
  Parameters parameters;
}
