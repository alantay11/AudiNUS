package com.orbital.audinus;

import android.graphics.Bitmap;

import java.io.Serializable;

public class AudioModel implements Serializable {

    private final String path;
    private final String title;
    private final String duration;
    private final Bitmap albumArt;

    public AudioModel(String path, String title, String duration, Bitmap albumArt) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.albumArt = albumArt;
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

    public Bitmap getAlbumArt() {
        return this.albumArt;
    }
}