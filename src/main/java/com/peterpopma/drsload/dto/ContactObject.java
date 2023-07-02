package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ContactObject {

  private String id;
  private String roid;
  private String name;
  private List<String> status;
  private List<ContactPostalInfo> postalInfo;
  private String voice;
  private String fax;
  private String email;
  private String clID;
  private String crID;
  private String crDate;
  private String upID;
  private String upDate;
  private String trDate;
  private ContactAuthInfo authInfo;
  private ContactDisclose disclose;

}
