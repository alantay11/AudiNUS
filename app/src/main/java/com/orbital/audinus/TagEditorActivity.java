package com.orbital.audinus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TagEditorActivity extends AppCompatActivity {

    AudioModel song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        song = getIntent().getParcelableExtra("SONG");

        setContentView(R.layout.activity_tag_editor);
    }
}