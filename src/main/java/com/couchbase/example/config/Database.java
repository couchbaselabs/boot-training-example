package com.couchbase.example.config;

import com.couchbase.client.CouchbaseClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration setup for the database.
 */
@Configuration
public class Database {

    static {
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
    }

    @Bean
    public List<URI> bootstrapNodes() throws Exception {
        return Arrays.asList(
          URI.create("http://127.0.0.1:8091/pools")
        );
    }

    @Bean
    public String bucket() throws Exception {
        return "default";
    }

    public String password() throws Exception {
        return "";
    }

    @Bean
    public CouchbaseClient couchbaseClient() throws Exception {
        return new CouchbaseClient(
            bootstrapNodes(),
            bucket(),
            password()
        );
    }

}
