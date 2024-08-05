package com.peterpopma.drsload.dto.epp;

public record Command(
        EppCommand commandType,
        ContactObject contactObject,
        DomainObject domainObject,
        HostObject hostObject,
        OrganizationObject organizationObject
) {
}

