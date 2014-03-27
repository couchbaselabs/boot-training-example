package com.couchbase.example.domain;

public class Session {

    private String username;
    private long created;

    public Session() {
    }

    public Session(String username, long created) {
        this.username = username;
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
