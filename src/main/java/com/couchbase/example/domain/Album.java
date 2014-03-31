package com.couchbase.example.domain;

import java.util.List;

public class Album {

    private String artist;
    private String title;
    private int numTracks;
    private List<Track> tracks;

    public Album() {

    }

    public Album(String artist, String title, int numTracks, List<Track> tracks) {
        this.artist = artist;
        this.title = title;
        this.numTracks = numTracks;
        this.tracks = tracks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public static class Track {
        private String name;
        private int length;

        public Track() {
        }

        public Track(String name, int length) {
            this.name = name;
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }

}
