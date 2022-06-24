package com.orbital.audinus;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.Objects;

public class TagEditorActivity extends AppCompatActivity {

    AudioModel song;
    AudioFile songFile;
    Tag tag;
    //String TAG = "TAG";
    TextInputLayout title, artist, album, composer, year, discNo, track, comment;
    ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);

        song = getIntent().getParcelableExtra("SONG");

        try {
            songFile = AudioFileIO.read(new File(song.getPath()));
            tag = songFile.getTag();

            title = findViewById(R.id.tag_song_title);
            artist = findViewById(R.id.tag_artist);
            album = findViewById(R.id.tag_album);
            composer = findViewById(R.id.tag_composer);
            year = findViewById(R.id.tag_year);
            discNo = findViewById(R.id.tag_disc_no);
            track = findViewById(R.id.tag_track);
            comment = findViewById(R.id.tag_comment);
            saveButton = findViewById(R.id.tag_save_button);


            Objects.requireNonNull(title.getEditText()).setText(tag.getFirst(FieldKey.TITLE));
            Objects.requireNonNull(artist.getEditText()).setText(tag.getFirst(FieldKey.ARTIST));
            Objects.requireNonNull(album.getEditText()).setText(tag.getFirst(FieldKey.ALBUM));
            Objects.requireNonNull(composer.getEditText()).setText(tag.getFirst(FieldKey.COMPOSER));
            Objects.requireNonNull(year.getEditText()).setText(tag.getFirst(FieldKey.YEAR));
            Objects.requireNonNull(discNo.getEditText()).setText(tag.getFirst(FieldKey.DISC_NO));
            Objects.requireNonNull(track.getEditText()).setText(tag.getFirst(FieldKey.TRACK));
            Objects.requireNonNull(comment.getEditText()).setText(tag.getFirst(FieldKey.COMMENT));

            saveButton.setOnClickListener(v -> save());

            /*Iterator iterator = tag.getFields();
            while(iterator.hasNext())
            {
                Log.d(TAG, iterator.next().toString());
            }*/
        } catch (Exception e) {
            Toast.makeText(this, "This file cannot be edited", Toast.LENGTH_SHORT).show();
        }
    }

    private void save() {
        try {
            tag.setField(FieldKey.TITLE, Objects.requireNonNull(title.getEditText()).getText().toString());
            tag.setField(FieldKey.ARTIST, Objects.requireNonNull(artist.getEditText()).getText().toString());
            tag.setField(FieldKey.ALBUM, Objects.requireNonNull(album.getEditText()).getText().toString());
            tag.setField(FieldKey.COMPOSER, Objects.requireNonNull(composer.getEditText()).getText().toString());
            tag.setField(FieldKey.YEAR, Objects.requireNonNull(year.getEditText()).getText().toString());
            tag.setField(FieldKey.DISC_NO, Objects.requireNonNull(discNo.getEditText()).getText().toString());
            tag.setField(FieldKey.TRACK, Objects.requireNonNull(track.getEditText()).getText().toString());
            tag.setField(FieldKey.COMMENT, Objects.requireNonNull(comment.getEditText()).getText().toString());
            songFile.commit();
            Toast.makeText(this, "Your changes have been saved", Toast.LENGTH_SHORT).show();
        } catch (FieldDataInvalidException e) {
            Toast.makeText(this, "You have input invalid metadata", Toast.LENGTH_SHORT).show();
        } catch (CannotWriteException e) {
            Toast.makeText(this, "This file cannot be saved", Toast.LENGTH_SHORT).show();        }
    }
}