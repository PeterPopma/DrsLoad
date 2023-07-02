package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPostalInfo {
  private String name;
  private String org;
  private ContactAddress addr;
}
