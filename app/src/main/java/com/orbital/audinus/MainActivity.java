package com.orbital.audinus;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity implements MediaPlayer.OnCompletionListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentContainerView fragmentContainerView;
    static SlidingUpPanelLayout slidingLayout;
    static Context context;

    TextView titleTextView, currentTimeTextView, totalTimeTextView, bitDepthTextView, sampleRateTextView;
    SeekBar seekBar;
    ImageView playPauseButton, nextButton, previousButton, albumArt, shuffleButton, repeatButton, equalizerButton;
    static ArrayList<AudioModel> songList = new ArrayList<>();
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    MediaSession mediaSession;
    FragmentContainerView bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkPermission()) {
            requestPermission();
            if (Build.VERSION.SDK_INT < 30) {
                onCreate(savedInstanceState);
            }
        }

        context = this;
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.views);
        viewPager = findViewById(R.id.viewpager);
        fragmentContainerView = findViewById(R.id.currently_playing_bar);

        slidingLayout = findViewById(R.id.sliding_layout);
        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.setPanelHeight(0);



        slidingLayout.setDragView(findViewById(R.id.song_title));

        bar = findViewById(R.id.currently_playing_bar);



        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID};


        Cursor cursor = getContentResolver().query(uri,
                projection, null, null, null);


        while (cursor.moveToNext()) {
            long albumID = cursor.getLong(3);
            String albumArt = "content://media/external/audio/albumart/" + albumID;

            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2), albumArt);

            if (new File(songData.getPath()).exists()) {
                songList.add(songData);
            }
        }
        cursor.close();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("SONGS", songList);
        SongsFragment frag = new SongsFragment();
        frag.setArguments(bundle);

            FragmentAdapter fragmentAdapter = new FragmentAdapter(this);
            fragmentAdapter.addFragment(frag, "songs");
            fragmentAdapter.addFragment(new PlaylistsFragment(), "playlists");
            fragmentAdapter.addFragment(new QueueFragment(), "queue");
            viewPager.setAdapter(fragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(fragmentAdapter.getTitle(position))
        ).attach();


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
    }


    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < 30) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Storage permissions are needed to manage your music", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                slidingLayout.setTouchEnabled(true);
            }

            @Override
            public void onPanelExpanded(View view) {
                slidingLayout.setTouchEnabled(false);
                bar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        slidingLayout.setTouchEnabled(true);
                        return false;
                    }
                });
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout.isLaidOut()) {
            slidingLayout.setPanelHeight(150);
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    ///////////////////////

    public void musicPlayer() {
        setResources();
        if (MyMediaPlayer.isPlayingSameSong()) {
            continueMusic();
        } else {
            playMusic();
        }


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    BottomBarFragment.progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTextView.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                    if (mediaPlayer.isPlaying()) {
                        playPauseButton.setImageResource(R.drawable.pause_48px);
                        BottomBarFragment.playPauseButton.setImageResource(R.drawable.pause_48px);
                        MyMediaPlayer.setCurrentTime(mediaPlayer.getCurrentPosition());
                    } else {
                        playPauseButton.setImageResource(R.drawable.play_arrow_48px);
                        BottomBarFragment.playPauseButton.setImageResource(R.drawable.play_arrow_48px);
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

                new Handler().postDelayed(this, 100);
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
                        BottomBarFragment.progressBar.setProgress(0);
                        MyMediaPlayer.setCurrentTime(0);
                    }
                } else {
                    mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                    seekBar.setProgress(MyMediaPlayer.getCurrentTime());
                    BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                    playPause();
                }
            }
        });
    }

    public void musicPlayer(ArrayList<AudioModel> filteredList) {
        songList = filteredList;
        musicPlayer();
    }

    void setResources() {
        currentSong = songList.get(MyMediaPlayer.getCurrentIndex());

        titleTextView.setText(currentSong.getTitle());
        totalTimeTextView.setText(convertToMMSS(currentSong.getDuration()));

        MyMediaPlayer.setPrevSong(MyMediaPlayer.getCurrentSong());
        MyMediaPlayer.setCurrentSong(currentSong.getPath());

        /*if (MyMediaPlayer.isPlayingSameSong()) {
            MyMediaPlayer.setCurrentSong(currentSong.getPath());
        } else {
            MyMediaPlayer.setPrevSong(MyMediaPlayer.getCurrentSong());
        }*/

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

        playPauseButton.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> prevButton());
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
            //Log.d("TAG", "this is the problem part before start?");
            mediaPlayer.start();
            //Log.d("TAG", "after start?");
            if (MyMediaPlayer.isPlayingSameSong()) {
                mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
            } else {
                seekBar.setProgress(0);
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                MyMediaPlayer.setPrevSong(MyMediaPlayer.getCurrentSong());
            }
            seekBar.setMax(mediaPlayer.getDuration());
            BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        createNotification();
    }

    public void repeatMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void continueMusic() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
        seekBar.setMax(mediaPlayer.getDuration());
        BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
    }

    public void playNextSong() { Log.d("TAG", MyMediaPlayer.getCurrentIndex() + " current " + (songList.size() - 1) +" max");
        if (MyMediaPlayer.getCurrentIndex() < songList.size() - 1) {
            MyMediaPlayer.nextSong();
            setResources();
            playMusic();
        } else {
            Toast.makeText(this, "You've reached the last song", Toast.LENGTH_SHORT).show();
        }
    }

    public void prevButton() {Log.d("TAG", MyMediaPlayer.getCurrentIndex() + " current " + (songList.size() - 1) +" max");
        if (MyMediaPlayer.getCurrentIndex() != 0) {
            if (mediaPlayer.getCurrentPosition() >= 5000) {
                mediaPlayer.seekTo(0);
                setResources();
            } else {
                MyMediaPlayer.prevSong();
                setResources();
                playMusic();
            }
        } else {
            Toast.makeText(this, "You've reached the first song", Toast.LENGTH_SHORT).show();
        }
    }

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //NotificationManagerCompat.from(this).cancelAll();
        } else {
            mediaPlayer.start();
            //createNotification();
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



    private void createNotification() {

        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.parse(currentSong.getAlbumArt()));
        Bitmap art = null;
        try {
            art = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initMediaSessions();


        Notification notification = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.music_note_48px)
                .setLargeIcon(art)
                .setContentTitle(currentSong.getTitle())
                .setContentIntent(intentCreator())
                /*.addAction(new NotificationCompat.Action(
                        R.drawable.skip_previous_48px, "prev",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.pause_48px, "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.skip_next_48px, "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))*/
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        //.setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(MediaSessionCompat.Token.fromToken(this.mediaSession.getSessionToken())))

                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManagerCompat.from(this).notify(0, notification);
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
                                         Log.d( "MediaPlayerService", "onSkipToNext");
                                     }

                                     @Override
                                     public void onSkipToPrevious() {
                                         prevButton();
                                         Log.d( "MediaPlayerService", "onSkipToPrevious");
                                     }
                                 }
        );
    }

    private PendingIntent intentCreator() {
        Intent notificationIntent = new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
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


    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format(Locale.ENGLISH,"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}