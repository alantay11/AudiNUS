package com.orbital.audinus;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MiniPlayListAdapter extends PlayListAdapter{// this is for playlistselection when choosing playlist to add the song into
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
                /*
                //MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putParcelableArrayListExtra("LIST", songList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (MyMediaPlayer.isPlayingSameSong()) { //prevents crash but causes progressbar to freak out sometimes
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }
                context.startActivity(intent);
                */

                ArrayList<AudioModel> x = PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(holder.getAdapterPosition()));
                x.add(song);



                FileOutputStream fos = null;
                String a = "";
                for(String y : PlaylistsFragment.playlists.keySet()) {
                    a = a + y + "!@#";
                    for (AudioModel z : PlaylistsFragment.playlists.get(y)) {
                        a = a + z.getTitle() + ";;;";
                    }
                    a += "\n";
                }
                try {
                    fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
                    fos.write(a.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, song.getTitle()+" added to playlist " + PlaylistsFragment.nameList.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                SongsFragment.dialog.dismiss();
            }
        });
    }
}
