package com.peterpopma.drsload.dto.api;

import com.peterpopma.drsload.dto.epp.HostAddr;

import java.util.List;

public class JSONBuilder {

    public String getContactJSON(String name, String street, String city, String postalCode, String telephone, String email) {
        return "{ \"id\": \"sh8013\"," +
                "\"postalInfo\": {" +
                "\"name\": \"Handige Harry\"," +
                "\"org\": \"De Klusjeman BV\"," +
                "\"addr\": {" +
                "\"street\": [" +
                "\"IJsselkade\"," +
                "\"100\"" +
                "]," +
                "\"city\": \"Amsterdam\"," +
                "\"sp\": \"Limburg\"," +
                "\"pc\": \"1234AA\"," +
                "\"cc\": \"NL\"" +
                "}" +
                "}," +
                "\"voice\": \"+31.612345678\"," +
                "\"fax\": \"+31.204578274\"," +
                "\"email\": \"epptestteam@sidn.nl\"," +
                "\"authInfo\": {" +
                "\"pw\": \"\"" +
                "}," +
                "\"disclose\": {" +
                " \"addr\": {" +
                "}" +
                " }" +
                "}}";
    }

    public String getDomainJSON(String domainName, List<String> hosts, String registrant, String adminC, List<String> techCs) {
        return "\"name\": \"domeinnaam.nl\"," +
                "\"period\": {" +
                "\"@unit\": \"m\"," +
                "\"#text\": \"3\"" +
                "}," +
                "\"registrant\": \"ADM000001-SIDN0\"," +
                "\"contact\": [" +
                "{" +
                "\"@type\": \"admin\"," +
                "\"#text\": \"ADM000001-SIDN0\"" +
                "}," +
                "{" +
                "\"@type\": \"tech\"," +
                "\"#text\": \"ADM000001-SIDN0\"" +
                "}" +
                "]," +
                "\"authInfo\": {" +
                "\"pw\": null" +
                "}";
    }

    public String getHostJSON(String hostname, List<HostAddr> ipAddresses) {
        return "{" +
                "\"name\": \"ns.notinzone.net\"," +
                "\"addr\": {" +
                "\"@ip\": \"v4\"," +
                "\"#text\": \"1.2.3.4\"" +
                "}" +
                "}";
    }
}