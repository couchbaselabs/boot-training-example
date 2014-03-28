package com.couchbase.example.web;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.example.domain.BlogPost;
import com.couchbase.example.domain.Cinema;
import com.couchbase.example.domain.Show;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RestController
@RequestMapping("/cinema")
public class CinemaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CinemaController.class);

    private final CouchbaseClient db;

    private final ObjectMapper mapper;

    @Autowired
    public CinemaController(CouchbaseClient db, ObjectMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @RequestMapping("/create/{name}")
    public ResponseEntity<String> create(@PathVariable String name) throws Exception {
        Cinema cinema = new Cinema();
        cinema.setName(name);

        OperationFuture<Boolean> future = db.add("cinema::" + name, mapper.writeValueAsString(cinema));
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/create/{} failed because of {}", name, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/play/{cinema}/{show}")
    public ResponseEntity<String> play(@PathVariable String cinema, @PathVariable String show) throws Exception {
        Show s = new Show();
        s.setCinema_id("cinema::" + cinema);
        s.setDuration(TimeUnit.HOURS.toSeconds(2));
        s.setName(show);
        s.setStart(System.currentTimeMillis() / 1000L);

        String showId = s.getCinema_id() + "::" + nextShowId(s.getCinema_id());
        OperationFuture<Boolean> future = db.add(showId, mapper.writeValueAsString(s));
        future.get();
        OperationStatus status = future.getStatus();
        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/play/{}/{} failed because of {}", cinema, show, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/listShows/{cinema}")
    public ResponseEntity<List<Show>> listShows(@PathVariable String cinema) throws Exception {
        int lastShowForCinema = Integer.parseInt((String) db.get("cinema::" + cinema + "::id"));

        ArrayList<String> keys = new ArrayList<String>(lastShowForCinema);
        for (int i = 1; i <= lastShowForCinema; i++) {
            keys.add("cinema::" + cinema + "::" + i);
        }

        Map<String, Object> shows = db.getBulk(keys);
        ArrayList<Show> showList = new ArrayList<Show>(shows.size());
        for (Object value : shows.values()) {
            showList.add(mapper.readValue((String) value, Show.class));
        }

        return new ResponseEntity<List<Show>>(showList, HttpStatus.OK);
    }

    private long nextShowId(String cinema) {
        return db.incr(cinema + "::id", 1, 1);
    }

}
