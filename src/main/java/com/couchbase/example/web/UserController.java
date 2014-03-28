package com.couchbase.example.web;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.example.domain.User;
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
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final CouchbaseClient db;

    private final ObjectMapper mapper;

    @Autowired
    public UserController(CouchbaseClient db, ObjectMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @RequestMapping("/save/{username}")
    public ResponseEntity<String> save(@PathVariable String username) throws Exception {
        long now = System.currentTimeMillis() / 1000L;
        User user = new User(username, true, now);

        OperationFuture<Boolean> future = db.set("user::" + username, mapper.writeValueAsString(user));
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/add/{} failed because of {}", username, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/read/{username}")
    public ResponseEntity<User> read(@PathVariable String username) throws Exception {
        String response = (String) db.get("user::" + username);
        if (response == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<User>(mapper.readValue(response, User.class), HttpStatus.OK);
        }
    }


}
