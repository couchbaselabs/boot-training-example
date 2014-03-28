package com.couchbase.example.web;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.example.domain.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/session")
public class SessionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);

    /**
     * Optional timeout in seconds.
     */
    private static final int TIMEOUT = 0;

    private final CouchbaseClient db;

    private final ObjectMapper mapper;

    @Autowired
    public SessionController(CouchbaseClient db, ObjectMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @RequestMapping("/login/{username}")
    public ResponseEntity<String> login(@PathVariable String username) throws Exception {
        long now = System.currentTimeMillis() / 1000L;
        Session session = new Session(username, now);

        OperationFuture<Boolean> future = db.add("session::" + username, TIMEOUT, mapper.writeValueAsString(session));
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>("logged in", HttpStatus.OK);
        } else if (status.getMessage().equals("Data exists for key")) {
            return new ResponseEntity<String>("already logged in", HttpStatus.OK);
        } else {
            LOGGER.warn("/login/{} failed because of {}", username, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/verify/{username}")
    public ResponseEntity<String> verify(@PathVariable String username) throws Exception {
        OperationFuture<Boolean> future = db.touch("session::" + username, TIMEOUT);
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.OK);
        } else if (status.getMessage().equals("Not Found")) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } else {
            LOGGER.warn("/verify/{} failed because of {}", username, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/logout/{username}")
    public ResponseEntity<String> logout(@PathVariable String username) throws Exception {
        OperationFuture<Boolean> future = db.delete("session::" + username);
        future.get();
        OperationStatus status = future.getStatus();

        if (!status.isSuccess()) {
           LOGGER.warn("/logout/{} failed because of {}", username, status.getMessage());
        }
        return new ResponseEntity<String>("logged out", HttpStatus.OK);
    }

}
