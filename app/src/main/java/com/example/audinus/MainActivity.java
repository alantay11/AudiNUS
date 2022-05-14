package com.example.audinus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.media.MediaPlayer;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        music = MediaPlayer.create(
                this, R.raw.miraihenokioku);
    }

    public void playSong(View v){
        music.start();
    }

    public void pauseSong(View v) {
        music.pause();
    }

    public void stopSong(View v) {
        music.stop();
        music = MediaPlayer.create(this, R.raw.miraihenokioku);
    }



}