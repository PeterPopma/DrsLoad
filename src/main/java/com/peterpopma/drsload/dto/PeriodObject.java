package com.peterpopma.drsload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PeriodObject {
  private Unit unit;
  private int value;
}
