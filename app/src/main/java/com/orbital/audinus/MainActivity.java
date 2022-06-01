package com.orbital.audinus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final ArrayList<AudioModel> songList = new ArrayList<>();
    private int lastPosition;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        TextView noMusicTextView = findViewById(R.id.no_songs_text);

        layoutManager = new LinearLayoutManager(this);

        if (!checkPermission()) {
            requestPermission();
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null, null);

        while (cursor.moveToNext()) {
            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));

            if(new File(songData.getPath()).exists()) {
                songList.add(songData);
            }

            if (songList.isEmpty()) {
                noMusicTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new MusicListAdapter(songList, getApplicationContext()));
            }
        }
        cursor.close();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
        PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Read permissions are required, please enable in settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerView != null) {
            recyclerView.setAdapter(new MusicListAdapter(songList, MainActivity.this));
        }
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPos", 0);
        recyclerView.scrollToPosition(lastPosition);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });
    }
    
    protected void onStop() {
        super.onStop();

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPos", lastPosition);
        e.apply();
    }
}