package com.orbital.audinus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class InsidePlaylistAdapter extends MusicListAdapter {
    private final String name;
    private Context mContext;

    public InsidePlaylistAdapter(int position, Context context) {
        super(PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(position)), context);
        this.name = PlaylistsFragment.nameList.get(position);
        this.mContext = context;
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
                popup.inflate(R.menu.insideplaylist_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AudioModel song = songList.get(holder.getAdapterPosition());
                        if (item.getItemId() == R.id.removeSong) {
                            songList.remove(song);
                            PlaylistsFragment.playlists.put(name, songList);
                            notifyItemRemoved(holder.getAdapterPosition());

                            FileOutputStream fos;
                            String a = "";
                            for (String y : PlaylistsFragment.playlists.keySet()) {
                                a += y + "!@#";
                                for (AudioModel z : Objects.requireNonNull(PlaylistsFragment.playlists.get(y))) {
                                    a += z.getTitle() + ";;;";
                                }
                                a += "\n";
                            }
                            try {
                                fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                                fos.write(a.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                //Intent intent = new Intent(context, MusicPlayerActivity.class);
                //intent.putParcelableArrayListExtra("LIST", songList);
                //intent.putExtra("PLAYLIST", samePlaylist);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /*if (MyMediaPlayer.isPlayingSameSong()) { //prevents crash but causes progressbar to freak out sometimes
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }*/
                //context.startActivity(intent);
                MainActivity ac = (MainActivity) MainActivity.context;
                ac.musicPlayer(songList);
                MainActivity.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                //((Activity)mContext).finish();
            }
        });

    }
}
