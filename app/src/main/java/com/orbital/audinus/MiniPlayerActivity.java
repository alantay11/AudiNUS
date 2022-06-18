package com.orbital.audinus;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;

public class MiniPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView titleTextView;
    AudioFile songFile;
    Tag tag;
    SeekBar seekBar;
    ImageView playPauseButton;
    String filename;
    Uri file;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_player);
        mediaPlayer.setOnCompletionListener(this);

        titleTextView = findViewById(R.id.mini_song_title);
        seekBar = findViewById(R.id.mini_seek_bar);
        playPauseButton = findViewById(R.id.mini_pause_play);

        titleTextView.setSelected(true);

        Intent intent = getIntent();
        file = intent.getData();
        if (file != null && intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_VIEW)) {
            String scheme = file.getScheme();
             filename = "";
            if ("file".equals(scheme)) {
                filename = file.getPath();
            } else {
                filename = file.toString();
            }
        }

        try {
            songFile = AudioFileIO.read(new File(file.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tag = songFile.getTag();
        titleTextView.setText(tag.getFirst(FieldKey.TITLE));
        playPauseButton.setOnClickListener(v -> playPause());

        playMusic();

        MiniPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    //currentTimeTextView.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        playPauseButton.setImageResource(R.drawable.pause_48px);
                        MyMediaPlayer.setCurrentTime(mediaPlayer.getCurrentPosition());
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    MyMediaPlayer.setCurrentTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer.isPlaying()) {
                    playPause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MyMediaPlayer.getCurrentTime() >= mediaPlayer.getDuration()) {
                        /*seekBar.setProgress(0);
                        MyMediaPlayer.setCurrentTime(0);*/
                } else {
                    mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                    seekBar.setProgress(MyMediaPlayer.getCurrentTime());
                    playPause();
                }
            }
        });
    }

    private void playMusic() {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            MyMediaPlayer.setPrevIndex(MyMediaPlayer.getCurrentIndex());
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp){
            //playNextSong();
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        this.finish();
    }


    private void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
}