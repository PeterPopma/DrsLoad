package com.peterpopma.drsload.dto.epp;

import java.util.List;

public record OrganizationAddress (
   List<String> street,
   String city,
   String sp,
   String pc,
   String cc
) {}
