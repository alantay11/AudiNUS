package com.orbital.audinus;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MiniPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView titleTextView, currentTimeTextView, totalTimeTextView;
    AudioFile songFile;
    Tag tag;
    SeekBar seekBar;
    ImageView playPauseButton, albumArt;
    String filename;
    Uri file;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    private boolean dragging;
    static ArrayList<AudioModel> songList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_player);
        mediaPlayer.setOnCompletionListener(this);

        titleTextView = findViewById(R.id.mini_song_title);
        currentTimeTextView = findViewById(R.id.mini_current_time);
        totalTimeTextView = findViewById(R.id.mini_total_time);
        seekBar = findViewById(R.id.mini_seek_bar);
        playPauseButton = findViewById(R.id.mini_pause_play);
        albumArt = findViewById(R.id.mini_album_art);

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
            songFile = AudioFileIO.read(new File(Objects.requireNonNull(file).getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID};


        Cursor cursor = getContentResolver().query(uri,
                projection,
                android.provider.MediaStore.Audio.Media.DATA + " like ? ",
                new String[] {file.getPath()},
                null);

        while (cursor.moveToNext()) {
            long albumID = cursor.getLong(3);
            String albumArt = "content://media/external/audio/albumart/" + albumID;

            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2), albumArt);

            if (new File(songData.getPath()).exists()) {
                songList.add(songData);
            }
        }
        cursor.close();

        AudioModel currentSong = songList.get(0);

        Glide.with(this)
                .load(currentSong.getAlbumArt())
                .placeholder(R.drawable.music_note_48px)
                .into(albumArt);

        tag = songFile.getTag();
        titleTextView.setText(currentSong.getTitle());
        totalTimeTextView.setText(MainActivity.convertToMMSS(currentSong.getDuration()));

        playPauseButton.setOnClickListener(v -> playPause());

        playMusic();

        MiniPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    if (!dragging) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                    currentTimeTextView.setText(MainActivity.convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        playPauseButton.setImageResource(R.drawable.pause_48px);
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                dragging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dragging = false;
                if (seekBar.getProgress() == seekBar.getMax()) {
                    mediaPlayer.pause();
                    currentTimeTextView.setText(MainActivity.convertToMMSS(currentSong.getDuration()));
                } else {
                    mediaPlayer.seekTo(seekBar.getProgress());
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
            MyMediaPlayer.setPrevSong(MyMediaPlayer.getCurrentSong());
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp){
        mediaPlayer.pause();
        //playNextSong();
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        this.finish();
    }

    private void playPause() {
        if (seekBar.getProgress() == seekBar.getMax()) {
            playMusic();
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
}