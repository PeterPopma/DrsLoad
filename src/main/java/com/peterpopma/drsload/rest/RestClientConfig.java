package com.peterpopma.drsload.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${drs.rest.uri}")
    String baseURI;

    @Bean
    RestClient restClient() {
        return RestClient.create(baseURI);
    }
}