package com.orbital.audinus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentbot#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentbot extends Fragment {
     static TextView textView;
     static SeekBar seekBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentbot, container, false);
        textView = view.findViewById(R.id.songName);
        textView.setText("No song playing");
        seekBar = view.findViewById(R.id.botBar);



        return view;
    }
}