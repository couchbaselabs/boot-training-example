package com.couchbase.example.web;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.example.domain.Album;
import com.couchbase.example.domain.BlogPost;
import com.couchbase.example.domain.Genre;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.internal.OperationCompletionListener;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@RestController
@RequestMapping("/genre")
public class GenreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CinemaController.class);

    private final CouchbaseClient db;

    private final ObjectMapper mapper;

    @Autowired
    public GenreController(CouchbaseClient db, ObjectMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @RequestMapping("/add/{name}")
    public ResponseEntity<String> add(@PathVariable String name) throws Exception {
        Genre genre = new Genre(name, Collections.<String>emptyList());

        OperationFuture<Boolean> future = db.add("genre::" + name, mapper.writeValueAsString(genre));
        future.get();
        OperationStatus status = future.getStatus();

        if (status.isSuccess()) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/add/{} failed because of {}", name, status.getMessage());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/addAlbum/{genre}/{album}")
    public ResponseEntity<String> addAlbum(final @PathVariable String genre, final @PathVariable String album) throws Exception {
        final CASValue<Object> foundGenre = db.gets("genre::" + genre);
        if(foundGenre == null) {
            return new ResponseEntity<String>("Genre not found", HttpStatus.NOT_FOUND);
        }

        Album randomAlbum = new Album("System Of A Down", "Toxicity", 15, Arrays.asList(
            new Album.Track("Deer Dance", 180),
            new Album.Track("Jet Pilot", 170),
            new Album.Track("Chop Suey!", 200)
        ));
        final String albumKey = "album::" + album;

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<CASResponse> casResponse = new AtomicReference<CASResponse>();
        db.add(albumKey, mapper.writeValueAsString(randomAlbum)).addListener(new OperationCompletionListener() {
            @Override
            public void onComplete(OperationFuture<?> future) throws Exception {
                OperationStatus status = future.getStatus();
                if (!status.isSuccess()) {
                    LOGGER.warn("/addAlbum/{}/{} failed because of {}", genre, album, status.getMessage());
                    return;
                }

                Genre convertedGenre = mapper.readValue((String) foundGenre.getValue(), Genre.class);
                convertedGenre.getAlbums().add(albumKey);
                casResponse.set(db.cas("genre::" + genre, foundGenre.getCas(), mapper.writeValueAsString(convertedGenre)));
                latch.countDown();
            }
        });

        latch.await();
        if (casResponse.get() == CASResponse.OK) {
            return new ResponseEntity<String>(HttpStatus.CREATED);
        } else {
            LOGGER.warn("/addAlbum/{}/{} failed because of {}", genre, album, casResponse.get());
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/read/{name}")
    public ResponseEntity<Map<String, Object>> read(@PathVariable String name) throws Exception {
        String result = (String) db.get("genre::" + name);
        if (result == null) {
            return new ResponseEntity<Map<String, Object>>(HttpStatus.NOT_FOUND);
        }

        Genre converted = mapper.readValue(result, Genre.class);
        Map<String, Object> fullAlbums = db.getBulk(converted.getAlbums());

        Map<String, Object> response = new HashMap<String, Object>();
        List<Album> albums = new ArrayList<Album>();
        response.put("title", converted.getTitle());
        response.put("albums", albums);

        for (Object value : fullAlbums.values()) {
            albums.add(mapper.readValue((String) value, Album.class));
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

}
