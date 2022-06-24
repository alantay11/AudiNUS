package com.orbital.audinus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class InsidePlaylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);
        TextView playlistName = findViewById(R.id.playlistName);
        playlistName.setText(PlaylistsFragment.nameList.get(PlaylistsFragment.position));
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new InsidePlaylistAdapter(PlaylistsFragment.position, this));


    }

}