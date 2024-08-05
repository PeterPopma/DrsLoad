package com.peterpopma.drsload.dto.epp;

import java.util.List;

public record HostObject (
   String roid,
   String name,
   String newName,
   List<String> status,
   List<HostAddr> addr,
   String clID,
   String crID,
   String crDate,
   String upID,
   String upDate,
   String trDate
) {}
