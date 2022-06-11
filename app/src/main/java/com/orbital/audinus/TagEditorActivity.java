package com.orbital.audinus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class TagEditorActivity extends AppCompatActivity {

    AudioModel song;
    AudioFile songFile;
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        song = getIntent().getParcelableExtra("SONG");

        try {
            songFile = AudioFileIO.read(new File(song.getPath()));
            Tag tag = songFile.getTag();
            Log.d(TAG, tag.getFirst(FieldKey.ARTIST) +
            tag.getFirst(FieldKey.ALBUM) +
            tag.getFirst(FieldKey.TITLE) +
            tag.getFirst(FieldKey.COMMENT) +
            tag.getFirst(FieldKey.YEAR) +
            tag.getFirst(FieldKey.TRACK) +
            tag.getFirst(FieldKey.DISC_NO) +
            tag.getFirst(FieldKey.COMPOSER) +
            tag.getFirst(FieldKey.ARTIST_SORT) + "damn it works");
        } catch (Exception e) {
            Toast.makeText(this, "This file cannot be edited", Toast.LENGTH_SHORT).show();
        }


        setContentView(R.layout.activity_tag_editor);
    }
}