package com.orbital.audinus;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MiniPlayListAdapter extends PlayListAdapter{// this is for playlist selection when choosing playlist to add the song into
    private static final String FILE_NAME = "example.txt";
    static AudioModel song;
    public MiniPlayListAdapter(HashMap<String, ArrayList<AudioModel>> songList, FragmentActivity context, ArrayList<String> playlists) {
        super(songList, context, playlists);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.ViewHolder holder, int position) {
        String songData = playlists.get(holder.getAdapterPosition());
        holder.titleTextView.setText(songData);



        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { //create new activity for playlist

                ArrayList<AudioModel> x = PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(holder.getAdapterPosition()));
                Objects.requireNonNull(x).add(song);


                FileOutputStream fos;
                StringBuilder a = new StringBuilder();
                for(String y : PlaylistsFragment.playlists.keySet()) {
                    a.append(y).append("!@#");
                    for (AudioModel z : Objects.requireNonNull(PlaylistsFragment.playlists.get(y))) {
                        a.append(z.getTitle()).append(";;;");
                    }
                    a.append("\n");
                }
                try {
                    fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    fos.write(a.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, song.getTitle()+" added to playlist " + PlaylistsFragment.nameList.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                SongsFragment.dialog.dismiss();
            }
        });
    }
}
