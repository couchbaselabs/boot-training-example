package com.couchbase.example.domain;

import java.util.ArrayList;
import java.util.List;

public class BlogPost {

    private String title;

    private List<Comment> comments;

    public BlogPost() {

    }
    public BlogPost(String title) {
        this(title, new ArrayList<Comment>());
    }

    public BlogPost(String title, List<Comment> comments) {
        this.title = title;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static class Comment {

        private String email;
        private String content;

        public Comment() {

        }

        public Comment(String email, String content) {
            this.email = email;
            this.content = content;
        }

        public String getEmail() {
            return email;
        }

        public String getContent() {
            return content;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
