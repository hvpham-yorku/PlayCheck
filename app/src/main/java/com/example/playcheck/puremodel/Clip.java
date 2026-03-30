package com.example.playcheck.puremodel;

import java.io.Serializable;

// Model class to represent a video clip
public class Clip implements Serializable {
    private String id;
    private String title;
    private String uri;

    public Clip() {} // Required for Firebase

    public Clip(String id, String title, String uri) {
        this.id = id;
        this.title = title;
        this.uri = uri;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
}
