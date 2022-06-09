package com.orbital.audinus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentBottomBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentBottomBar extends Fragment {
    static TextView songName;
    static ImageView playPauseButton;
    static ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);

        songName = view.findViewById(R.id.songName);
        songName.setText(R.string.NoSongBottomBarText);
        songName.setSelected(true);

        playPauseButton = view.findViewById(R.id.pause_play);

        progressBar = view.findViewById(R.id.progressBar);



        return view;
    }
}