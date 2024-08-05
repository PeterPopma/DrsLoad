package com.peterpopma.drsload.dto.epp;

import java.util.List;


public record ContactObject (

   String id,
   String roid,
   String name,
   List<String> status,
   List<ContactPostalInfo> postalInfo,
   String voice,
   String fax,
   String email,
   String clID,
   String crID,
   String crDate,
   String upID,
   String upDate,
   String trDate,
   ContactAuthInfo authInfo,
   ContactDisclose disclose

) {}
