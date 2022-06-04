package com.orbital.audinus;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView titleTextView, currentTimeTextView, totalTimeTextView;
    SeekBar seekBar;
    ImageView playPauseButton, nextButton, previousButton, albumArt, shuffleButton, repeatButton;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    //private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        mediaPlayer.setOnCompletionListener(this);

        titleTextView = findViewById(R.id.song_title);
        currentTimeTextView = findViewById(R.id.current_time);
        totalTimeTextView = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.pause_play);
        nextButton = findViewById(R.id.next);
        previousButton = findViewById(R.id.previous);
        shuffleButton = findViewById(R.id.shuffle);
        repeatButton = findViewById(R.id.repeat);
        albumArt = findViewById(R.id.album_art);

        titleTextView.setSelected(true);

        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResources();
        if (MyMediaPlayer.getPrevIndex() == MyMediaPlayer.getCurrentIndex()) {
            continueMusic();
        } else {
            playMusic();
        }

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTextView.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        playPauseButton.setImageResource(R.drawable.pause_48px);
                        MyMediaPlayer.setCurrentTime(mediaPlayer.getCurrentPosition());
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                    }

                    if (MyMediaPlayer.isShuffle()) {
                        shuffleButton.setImageResource(R.drawable.shuffle_on_48px);
                    } else {
                        shuffleButton.setImageResource(R.drawable.shuffle_48px);
                    }

                    if (MyMediaPlayer.isShuffle()) {
                        repeatButton.setImageResource(R.drawable.repeat_on_48px);
                    } else {
                        repeatButton.setImageResource(R.drawable.repeat_48px);
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
                if (MyMediaPlayer.getCurrentTime() >= mediaPlayer.getDuration()){
                    playNextSong();
                    seekBar.setProgress(0);
                    MyMediaPlayer.setCurrentTime(0);
                } else {
                    mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                    seekBar.setProgress(MyMediaPlayer.getCurrentTime());
                    playPause();
                }
            }
        });


    }

    void setResources() {
        currentSong = songList.get(MyMediaPlayer.getCurrentIndex());

        titleTextView.setText(currentSong.getTitle());

        totalTimeTextView.setText(convertToMMSS(currentSong.getDuration()));

        albumArt.setImageBitmap(currentSong.getAlbumArt());

        playPauseButton.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> backButtonAction());
        repeatButton.setOnClickListener(v -> toggleRepeat());
        shuffleButton.setOnClickListener(v -> toggleShuffle());
    }

    private void playMusic() {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (MyMediaPlayer.getPrevIndex() == MyMediaPlayer.getCurrentIndex()){
                mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            } else {
                seekBar.setProgress(0);
                MyMediaPlayer.setPrevIndex(MyMediaPlayer.getCurrentIndex());
            }

            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartMusic() {
        //Log.d(TAG, "restarting music");
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void continueMusic() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void playNextSong() {
        if (MyMediaPlayer.getCurrentIndex() != songList.size() - 1) {
            MyMediaPlayer.nextSong();
            setResources();
            playMusic();
        }
    }

    private void backButtonAction() {
        if (MyMediaPlayer.getCurrentIndex() != 0) {
            if (mediaPlayer.getCurrentPosition() >= 5000) {
                mediaPlayer.seekTo(0);
                setResources();
            } else {
                MyMediaPlayer.prevSong();
                setResources();
                playMusic();
            }
        }
    }

    private void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    private void toggleRepeat() {
        MyMediaPlayer.toggleRepeat();
        //Log.d(TAG, "toggling repeat");
    }

    private void toggleShuffle() {
        MyMediaPlayer.toggleShuffle();
        //Log.d(TAG, "toggling shuffle");
    }


    @Override
    public void onCompletion(MediaPlayer mp){

        // check if repeat is ON or OFF
        if (MyMediaPlayer.isRepeat()) {
            // repeat is on, play same song again
            //Log.d(TAG, "repeat is true");
            restartMusic();
        } else if (MyMediaPlayer.isShuffle()) {
            // shuffle is on - play a random song
            //Log.d(TAG, "shuffle is true");
            Random rand = new Random();
            MyMediaPlayer.setCurrentIndex(rand.nextInt((songList.size() - 1) + 1));
            setResources();
            playMusic();
        } else {
            playNextSong();
        }
    }


    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}