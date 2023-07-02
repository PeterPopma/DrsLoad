package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HostObject {
  private String roid;
  private String name;
  private String newName;
  private List<String> status;
  private List<HostAddr> addr;
  private String clID;
  private String crID;
  private String crDate;
  private String upID;
  private String upDate;
  private String trDate;
}
