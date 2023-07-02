package com.peterpopma.drsload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
  private String commandType;
  private ContactObject contactObject;
  private DomainObject domainObject;
  private HostObject hostObject;
  private OrganizationObject organizationObject;

}
