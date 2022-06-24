package com.orbital.audinus;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InsidePlaylistAdapter extends MusicListAdapter{
    private final String name;
    public InsidePlaylistAdapter(int position, Context context) {
        super(PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(position)), context);
        this.name = PlaylistsFragment.nameList.get(position);
    }


    @Override
    public void onBindViewHolder(MusicListAdapter.ViewHolder holder, int position) {
        AudioModel songData = songList.get(holder.getAdapterPosition());
        holder.titleTextView.setText(songData.getTitle());
        Glide.with(context)
                .load(songData.getAlbumArt())
                .placeholder(R.drawable.music_note_48px)
                .into(holder.albumArtRImageView);

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.menuButton);
                //inflating menu from xml
                popup.inflate(R.menu.insideplaylist_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AudioModel song = songList.get(holder.getAdapterPosition());
                        switch (item.getItemId()) {
                            case R.id.removeSong:
                                    songList.remove(song);
                                    PlaylistsFragment.playlists.put(name, songList);
                                    notifyItemRemoved(holder.getAdapterPosition());

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

                                break;

                        }
                        return false;
                    }
                });
                popup.show();
            }});

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMediaPlayer.currentIndex = holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putParcelableArrayListExtra("LIST", songList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (MyMediaPlayer.isPlayingSameSong()) { //prevents crash but causes progressbar to freak out sometimes
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }
                context.startActivity(intent);

            }
        });

    }
}
