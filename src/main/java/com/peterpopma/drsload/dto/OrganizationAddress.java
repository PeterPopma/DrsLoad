package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganizationAddress {
  private List<String> street;
  private String city;
  private String sp;
  private String pc;
  private String cc;
}
