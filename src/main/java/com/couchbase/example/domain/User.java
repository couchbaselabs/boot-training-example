package com.couchbase.example.domain;

public class User {

    private String username;
    private boolean active;
    private long created;

    public User() {

    }

    public User(String username, boolean active, long created) {
        this.username = username;
        this.active = active;
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
