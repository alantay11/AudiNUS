package com.orbital.audinus;

import android.media.MediaPlayer;

public class MyMediaPlayer {

    public static MediaPlayer instance;

    public static int currentIndex = -1;
    public static int prevIndex = -2;
    public static int currentTime = 0;

    public static boolean isRepeat;
    public static boolean isShuffle;

    public static MediaPlayer getInstance(){
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static void nextSong() {
        MyMediaPlayer.currentIndex++;
    }

    public static void prevSong() {
        MyMediaPlayer.currentIndex--;
    }

    public static int getCurrentIndex() {
        return MyMediaPlayer.currentIndex;
    }

    public static int getPrevIndex() {
        return MyMediaPlayer.prevIndex;
    }

    public static int getCurrentTime() {
        return MyMediaPlayer.currentTime;
    }

    public static boolean isRepeat() {
        return MyMediaPlayer.isRepeat;
    }

    public static boolean isShuffle() {
        return MyMediaPlayer.isShuffle;
    }

    public static void setPrevIndex(int prevIndex) {
        MyMediaPlayer.prevIndex = prevIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        MyMediaPlayer.currentIndex = currentIndex;
    }

    public static void setCurrentTime(int currentPosition) {
        MyMediaPlayer.currentTime = currentPosition;
    }

    public static void toggleRepeat() {
        MyMediaPlayer.isRepeat = !MyMediaPlayer.isRepeat;
    }

    public static void toggleShuffle() {
        MyMediaPlayer.isShuffle = !MyMediaPlayer.isShuffle;
    }

}