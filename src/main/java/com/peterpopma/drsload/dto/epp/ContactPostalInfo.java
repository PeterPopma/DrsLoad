package com.peterpopma.drsload.dto.epp;

public record ContactPostalInfo(String name,
        String org,
        ContactAddress addr
) {}
