package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

enum Unit {
  Y,
  M
}

@Getter
@Setter
public class PeriodObject {
  private Unit unit;
  private int value;
}
