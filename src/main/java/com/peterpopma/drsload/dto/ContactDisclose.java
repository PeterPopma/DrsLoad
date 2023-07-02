package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDisclose {
  private String flag;
  private String nameInt;
  private String nameLoc;
  private String orgLoc;
  private String orgInt;
  private String addrLoc;
  private String addrInt;
  private String voice;
  private String fax;
  private String email;
}

