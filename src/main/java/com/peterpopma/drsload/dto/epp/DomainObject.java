package com.peterpopma.drsload.dto.epp;

import java.util.List;

public record DomainObject (
   String id,
   String roid,
   PeriodObject period,
   String status,
   String registrant,
   String adminC,
   List<String> techC,
   List<String> hosts,
   String clID,
   String crID,
   String crDate,
   String upID,
   String upDate,
   String exDate,
   String trDate,
   DomainAuthInfo authInfo

) {}
