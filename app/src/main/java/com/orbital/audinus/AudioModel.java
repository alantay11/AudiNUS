package com.orbital.audinus;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioModel implements Parcelable {

    private final String path;
    private final String title;
    private final String duration;
    private final String albumArt;

    public AudioModel(String path, String title, String duration, String albumArt) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.albumArt = albumArt;
    }

    protected AudioModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        duration = in.readString();
        albumArt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeString(albumArt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioModel> CREATOR = new Creator<AudioModel>() {
        @Override
        public AudioModel createFromParcel(Parcel in) {
            return new AudioModel(in);
        }

        @Override
        public AudioModel[] newArray(int size) {
            return new AudioModel[size];
        }
    };

    public String getPath() {
        return this.path;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDuration() {
        return this.duration;
    }

    public String getAlbumArt() {
        return this.albumArt;
    }
}