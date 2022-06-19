package com.orbital.audinus;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {


    TextView titleTextView, currentTimeTextView, totalTimeTextView, bitDepthTextView, sampleRateTextView;
    SeekBar seekBar;
    ImageView playPauseButton, nextButton, previousButton, albumArt, shuffleButton, repeatButton, equalizerButton;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    MediaSession mediaSession;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        mediaPlayer.setOnCompletionListener(this);


        titleTextView = findViewById(R.id.song_title);
        currentTimeTextView = findViewById(R.id.current_time);
        totalTimeTextView = findViewById(R.id.total_time);
        bitDepthTextView = findViewById(R.id.bit_depth);
        sampleRateTextView = findViewById(R.id.sample_rate);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.pause_play);
        nextButton = findViewById(R.id.next);
        previousButton = findViewById(R.id.previous);
        shuffleButton = findViewById(R.id.shuffle);
        repeatButton = findViewById(R.id.repeat);
        equalizerButton = findViewById(R.id.equalizer);
        albumArt = findViewById(R.id.album_art);

        titleTextView.setSelected(true);

        songList = getIntent().getParcelableArrayListExtra(("LIST"));

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
                    BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                    MainActivity.seekBar.setProgress(seekBar.getProgress());
                    currentTimeTextView.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if (mediaPlayer.isPlaying()) {
                        playPauseButton.setImageResource(R.drawable.pause_48px);
                        BottomBarFragment.playPauseButton.setImageResource(R.drawable.pause_48px);
                        MainActivity.playPauseButton.setImageResource(R.drawable.pause_48px);
                        MyMediaPlayer.setCurrentTime(mediaPlayer.getCurrentPosition());
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                        BottomBarFragment.playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                        MainActivity.playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                    }

                    if (MyMediaPlayer.isShuffle()) {
                        shuffleButton.setImageResource(R.drawable.shuffle_on_48px);
                    } else {
                        shuffleButton.setImageResource(R.drawable.shuffle_48px);
                    }

                    if (MyMediaPlayer.isRepeat()) {
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
                if (MyMediaPlayer.getCurrentTime() >= mediaPlayer.getDuration()) {
                    if (MyMediaPlayer.isRepeat()) {
                        repeatMusic();
                    } else if (MyMediaPlayer.isShuffle()) {
                        playRandomSong();
                    } else {
                        playNextSong();
                        seekBar.setProgress(0);
                        BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                        MainActivity.seekBar.setProgress(seekBar.getProgress());
                        MyMediaPlayer.setCurrentTime(0);
                    }
                } else {
                    mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                    seekBar.setProgress(MyMediaPlayer.getCurrentTime());
                    BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                    MainActivity.seekBar.setProgress(seekBar.getProgress());
                    playPause();
                }
            }
        });

        MainActivity.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    if (MyMediaPlayer.isRepeat()) {
                        repeatMusic();
                    } else if (MyMediaPlayer.isShuffle()) {
                        playRandomSong();
                    } else {
                        playNextSong();
                        seekBar.setProgress(0);
                        BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                        MainActivity.seekBar.setProgress(seekBar.getProgress());
                        MyMediaPlayer.setCurrentTime(0);
                    }
                } else {
                    mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                    seekBar.setProgress(MyMediaPlayer.getCurrentTime());
                    BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                    MainActivity.seekBar.setProgress(seekBar.getProgress());
                    playPause();
                }
            }
        });

    }


    void setResources() {
        currentSong = songList.get(MyMediaPlayer.getCurrentIndex());

        titleTextView.setText(currentSong.getTitle());
        totalTimeTextView.setText(convertToMMSS(currentSong.getDuration()));


        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(currentSong.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);

        //bitDepthTextView.setText(mf.getString(MediaFormat.KEY_PCM_ENCODING));
        sampleRateTextView.setText(String.format("%s%s", mf.getInteger(MediaFormat.KEY_SAMPLE_RATE) / 1000.0, getString(R.string.kHz)));

        Glide.with(this)
                .load(currentSong.getAlbumArt())
                .placeholder(R.drawable.music_note_48px)
                .into(albumArt);

        MainActivity.imageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(currentSong.getAlbumArt())
                .placeholder(R.drawable.music_note_48px)
                .into(MainActivity.imageView);


        //BottomBarFragment.setAlbumArt(currentSong.getAlbumArt());

        playPauseButton.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        MainActivity.nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> backButtonAction());
        MainActivity.previousButton.setOnClickListener(v -> backButtonAction());
        repeatButton.setOnClickListener(v -> toggleRepeat());
        shuffleButton.setOnClickListener(v -> toggleShuffle());
        equalizerButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MyMediaPlayer.getInstance().getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(intent, 13);
                getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                //Toast.makeText(this, "Your phone doesn't support equalization", Toast.LENGTH_SHORT).show();
            }
        });
        BottomBarFragment.songName.setText(currentSong.getTitle());
        BottomBarFragment.playPauseButton.setOnClickListener(v -> playPause());
        MainActivity.playPauseButton.setOnClickListener(v -> playPause());
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void playMusic() {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (MyMediaPlayer.isPlayingSameSong()){
                mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                MainActivity.seekBar.setProgress(seekBar.getProgress());
            } else {
                seekBar.setProgress(0);
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                MainActivity.seekBar.setProgress(seekBar.getProgress());
                MyMediaPlayer.setPrevIndex(MyMediaPlayer.getCurrentIndex());
            }

            seekBar.setMax(mediaPlayer.getDuration());
            BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
            MainActivity.seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        notificationChannel();
    }

    public void repeatMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
            MainActivity.seekBar.setProgress(seekBar.getProgress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void continueMusic() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
        MainActivity.seekBar.setProgress(seekBar.getProgress());
        seekBar.setMax(mediaPlayer.getDuration());
        BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
        MainActivity.seekBar.setMax(mediaPlayer.getDuration());
    }

    public void playNextSong() {
        if (MyMediaPlayer.getCurrentIndex() != songList.size() - 1) {
            MyMediaPlayer.nextSong();
            setResources();
            playMusic();
        } else {
            Toast.makeText(this, "You've reached the last song", Toast.LENGTH_SHORT).show();
        }
    }

    public void backButtonAction() {
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

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public void playRandomSong() {
        Random rand = new Random();
        MyMediaPlayer.setCurrentIndex(rand.nextInt((songList.size() - 1) + 1));
        setResources();
        playMusic();
    }

    public void toggleRepeat() {
        MyMediaPlayer.toggleRepeat();
    }

    public void toggleShuffle() {
        MyMediaPlayer.toggleShuffle();
    }

    public void notificationChannel() {
        Bitmap notifArt = null;
        try {
             notifArt = Glide.with(this)
                    .asBitmap()
                    .load(currentSong.getAlbumArt())
                    .placeholder(R.drawable.music_note_48px)
                    .submit()
                    .get();
        } catch (Exception e) {
            Log.d(TAG, "failed bitmap");
            e.printStackTrace();
        }
        initMediaSessions();

        Notification notification = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.music_note_48px)
                .setLargeIcon(notifArt)
                .setContentTitle(currentSong.getTitle())
                .addAction(new NotificationCompat.Action(
                        R.drawable.skip_previous_48px, "prev",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.pause_48px, "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.skip_next_48px, "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(MediaSessionCompat.Token.fromToken(this.mediaSession.getSessionToken())))

                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManagerCompat.from(this).notify(2, notification);
    }

    private void initMediaSessions() {

        mediaSession = new MediaSession(getApplicationContext(), "simple player session");

        mediaSession.setCallback(new MediaSession.Callback(){
                                 @Override
                                 public void onPlay() {
                                     playPause();
                                     Log.d( "MediaPlayerService", "onPlay");
                                 }

                                 @Override
                                 public void onPause() {
                                     playPause();
                                     Log.d( "MediaPlayerService", "onPause");
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     playNextSong();
                                     Log.d( "MediaPlayerService", "onSkipToNext");}

                                 @Override
                                 public void onSkipToPrevious() {
                                     backButtonAction();
                                     Log.d( "MediaPlayerService", "onSkipToPrevious");
                                 }
                             }
        );
    }


    @Override
    public void onCompletion(MediaPlayer mp){
        if (MyMediaPlayer.isRepeat()) {
            repeatMusic();
        } else if (MyMediaPlayer.isShuffle()) {
            playRandomSong();
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        MainActivity.slidingLayout.setPanelHeight(125);
        startActivity(intent);
    }
}