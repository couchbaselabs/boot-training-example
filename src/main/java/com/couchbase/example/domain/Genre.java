package com.couchbase.example.domain;

import java.util.List;

public class Genre {

    private String title;
    private List<String> albums;

    public Genre() {

    }

    public Genre(String title, List<String> albums) {
        this.title = title;
        this.albums = albums;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAlbums() {
        return albums;
    }

    public void setAlbums(List<String> albums) {
        this.albums = albums;
    }
}
