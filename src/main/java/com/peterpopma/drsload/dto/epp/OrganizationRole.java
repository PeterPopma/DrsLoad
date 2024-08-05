package com.peterpopma.drsload.dto.epp;

import java.util.List;

public record OrganizationRole (
   String type,
   String roleId,
   List<String> status

) {}
