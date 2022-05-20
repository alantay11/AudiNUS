package com.orbital.audinus;

import java.io.Serializable;

public class AudioModel implements Serializable {

    private final String path;
    private final String title;
    private final String duration;

    public AudioModel(String path, String title, String duration) {
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    public String getPath() {
        return this.path;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDuration() {
        return this.duration;
    }
}