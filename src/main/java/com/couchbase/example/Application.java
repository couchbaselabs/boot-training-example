package com.couchbase.example;

import com.couchbase.example.config.Database;
import com.couchbase.example.config.Mapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Centralized starting point for the application, importing configuration and
 * helping with bootstrap.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.couchbase.example"})
@Import({Database.class, Mapping.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
