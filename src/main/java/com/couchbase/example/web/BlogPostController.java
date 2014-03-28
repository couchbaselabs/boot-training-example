package com.couchbase.example.web;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.example.domain.BlogPost;
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

import java.util.Arrays;

@Controller
@RestController
@RequestMapping("/blog")
public class BlogPostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogPostController.class);

    private final CouchbaseClient db;

    private final ObjectMapper mapper;

    @Autowired
    public BlogPostController(CouchbaseClient db, ObjectMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @RequestMapping("/create")
    public ResponseEntity<String> create() throws Exception {

        BlogPost.Comment comment = new BlogPost.Comment("foo@bar.com", "some nice content");

        String title = "A post title";
        BlogPost post = new BlogPost(title, Arrays.asList(comment));
        OperationFuture<Boolean> future = db.add("blogpost::" + nextPostId(), mapper.writeValueAsString(post));
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/create/{} failed because of {}", title, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private long nextPostId() {
        return db.incr("blogpost::id", 1, 1);
    }

    @RequestMapping("/read/{id}")
    public ResponseEntity<BlogPost> read(@PathVariable int id) throws Exception {
        String result = (String) db.get("blogpost::" + id);
        if (result == null) {
            return new ResponseEntity<BlogPost>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<BlogPost>(mapper.readValue(result, BlogPost.class), HttpStatus.OK);
    }
}
