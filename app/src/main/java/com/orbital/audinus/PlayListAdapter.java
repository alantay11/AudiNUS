package com.orbital.audinus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    HashMap<String,ArrayList<AudioModel>> songList;
    final Context context;
    ArrayList<String> playlists;
    private static final String FILE_NAME = "example.txt";


    public PlayListAdapter(HashMap<String, ArrayList<AudioModel>> songList, FragmentActivity context, ArrayList<String> playlists) {
        this.songList = songList;
        this.context = context;
        this.playlists= playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.ViewHolder holder, int position) {
        String songData = playlists.get(holder.getAdapterPosition());
        holder.titleTextView.setText(songData);



        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { //create new activity for playlist

                PlaylistsFragment.position= holder.getAdapterPosition();
                Intent intent = new Intent(context, InsidePlaylist.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.menuButton);
                popup.inflate(R.menu.playlist_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.deletePlaylist) {
                                String playlistName = PlaylistsFragment.nameList.get(holder.getAdapterPosition());
                                if (playlistName.equals("Favourites")) {
                                    Toast.makeText(context, "Favourites cannot be deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    PlaylistsFragment.playlists.remove(playlistName);
                                    PlaylistsFragment.nameList.remove(playlistName);
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    FileOutputStream fos;
                                    try {
                                        String x = "";
                                        for (String y : PlaylistsFragment.nameList) {
                                            x += y + "!@#";
                                            for (AudioModel z : Objects.requireNonNull(PlaylistsFragment.playlists.get(y))) {
                                                x += z.getTitle() + ";;;";
                                            }
                                            x += "\n";
                                        }
                                        fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                                        fos.write(x.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView albumArtRImageView;
        ImageButton menuButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.song_title_recycler);
            albumArtRImageView = itemView.findViewById(R.id.album_art_recycler);
            menuButton = itemView.findViewById(R.id.menu_button_recycler);
        }
    }
}

