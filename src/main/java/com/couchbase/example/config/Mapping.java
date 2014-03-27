package com.couchbase.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration setup for data mapping.
 */
@Configuration
public class Mapping {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
