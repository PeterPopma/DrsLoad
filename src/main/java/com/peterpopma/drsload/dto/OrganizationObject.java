package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganizationObject {
  private String id;
  private String roid;
  private OrganizationRole role;
  private String status;
  private List<OrganizationPostalInfo> postalInfoList;

  private String voice;
  private String fax;
  private String email;
  private String url;
  private List<OrganizationContact> contacts;
  private String crID;
  private String crDate;
  private String upID;
  private String upDate;
}
