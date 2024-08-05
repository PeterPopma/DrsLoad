package com.peterpopma.drsload.dto.epp;

public record OrganizationPostalInfo (
   String name,
   String org,
   OrganizationAddress addr
) {}
