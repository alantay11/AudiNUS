package com.orbital.audinus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragmentV1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragmentV1 extends Fragment implements MediaPlayer.OnCompletionListener{

    TextView titleTextView, currentTimeTextView, totalTimeTextView, bitDepthTextView, sampleRateTextView;
    SeekBar seekBar;
    ImageView playPauseButton, nextButton, previousButton, albumArt, shuffleButton, repeatButton, equalizerButton;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_v1, container, false);

        mediaPlayer.setOnCompletionListener(this);

        titleTextView = view.findViewById(R.id.song_title1);
        currentTimeTextView = view.findViewById(R.id.current_time1);
        totalTimeTextView = view.findViewById(R.id.total_time1);
        bitDepthTextView = view.findViewById(R.id.bit_depth1);
        sampleRateTextView = view.findViewById(R.id.sample_rate1);
        seekBar = view.findViewById(R.id.seek_bar1);
        playPauseButton = view.findViewById(R.id.pause_play1);
        nextButton = view.findViewById(R.id.next1);
        previousButton = view.findViewById(R.id.previous1);
        shuffleButton = view.findViewById(R.id.shuffle1);
        repeatButton = view.findViewById(R.id.repeat1);
        equalizerButton = view.findViewById(R.id.equalizer1);
        albumArt = view.findViewById(R.id.album_art1);

        titleTextView.setSelected(true);
        songList = this.getActivity().getIntent().getParcelableArrayListExtra(("LIST"));

        setResources();


        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
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

        return view;
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

        //BottomBarFragment.setAlbumArt(currentSong.getAlbumArt());

        playPauseButton.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        previousButton.setOnClickListener(v -> backButtonAction());
        repeatButton.setOnClickListener(v -> toggleRepeat());
        shuffleButton.setOnClickListener(v -> toggleShuffle());
        equalizerButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MyMediaPlayer.getInstance().getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getActivity().getPackageName());
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(intent, 13);
                getActivity().getApplicationContext().startActivity(intent);
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

    private void playMusic() {

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (MyMediaPlayer.isPlayingSameSong()){
                mediaPlayer.seekTo(MyMediaPlayer.getCurrentTime());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
            } else {
                seekBar.setProgress(0);
                BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
                MyMediaPlayer.setPrevIndex(MyMediaPlayer.getCurrentIndex());
            }

            seekBar.setMax(mediaPlayer.getDuration());
            BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void repeatMusic() {
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

    private void continueMusic() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        BottomBarFragment.progressBar.setProgress(seekBar.getProgress());
        seekBar.setMax(mediaPlayer.getDuration());
        BottomBarFragment.progressBar.setMax(mediaPlayer.getDuration());
    }

    private void playNextSong() {
        if (MyMediaPlayer.getCurrentIndex() != songList.size() - 1) {
            MyMediaPlayer.nextSong();
            setResources();
            playMusic();
        } else {
            Toast.makeText(this.getActivity(), "You've reached the last song", Toast.LENGTH_SHORT).show();
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

    private void playRandomSong() {
        Random rand = new Random();
        MyMediaPlayer.setCurrentIndex(rand.nextInt((songList.size() - 1) + 1));
        setResources();
        playMusic();
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
    /*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getContext().getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
*/

}