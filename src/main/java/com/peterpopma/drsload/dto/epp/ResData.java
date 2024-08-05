package com.peterpopma.drsload.dto.epp;

public record ResData(
        ContactObject contactObject,
        DomainObject domainObject,
        HostObject hostObject
) {
}
