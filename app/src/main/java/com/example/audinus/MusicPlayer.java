package com.example.audinus;

import android.media.MediaPlayer;
import android.view.View;

public class MusicPlayer {


    MediaPlayer music;
    MainActivity main;

    MusicPlayer(MainActivity main) {
        music = MediaPlayer.create(main, R.raw.song);
    }


    public void playSong(View v){
        music.start();
    }

    public void pauseSong(View v) {
        music.pause();
    }

    public void stopSong(View v) {
        music.stop();
        music = MediaPlayer.create(main, R.raw.song);
    }
}
