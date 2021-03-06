package com.orbital.audinus;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    static ArrayList<AudioModel> songList;
    final Context context;

    public QueueAdapter(ArrayList<AudioModel> songsList, Context context) {
        songList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueueAdapter.ViewHolder holder, int position) {
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
                popup.inflate(R.menu.queue_menu);
            //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AudioModel song;
                    if (item.getItemId() == R.id.tagEditor) {
                        MediaPlayer mp = MyMediaPlayer.getInstance();
                        if (mp.isPlaying()) {
                            mp.pause();
                            Toast.makeText(context, "Playback has paused to allow editing", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(context, TagEditorActivity.class);
                        intent.putExtra("SONG", songList.get(holder.getAdapterPosition()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (item.getItemId() == R.id.removeFromQueue) {
                        song = songList.get(holder.getAdapterPosition());
                        QueueFragment.songList.remove(song);
                        songList.remove(song);
                        Toast.makeText(context, song.getTitle() + " removed from queue", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else if (item.getItemId() == R.id.addToPlaylist) {
                            if (PlaylistsFragment.playlists.size() == 0) {
                                Toast.makeText(context, "You haven't created any playlists yet", Toast.LENGTH_SHORT).show();
                            } else {
                                /*
                                //hardcode to first playlist to test
                                ArrayList<AudioModel> x = PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(0));
                                song = songList.get(holder.getAdapterPosition());
                                x.add(song);
                                PlaylistsFragment.playlists.put(PlaylistsFragment.nameList.get(0),x);
                                PlaylistsFragment.nameList.add(song.getTitle());

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

                                Toast.makeText(context, song.getTitle()+" added to playlist " + PlaylistsFragment.nameList.get(0), Toast.LENGTH_SHORT).show();

                                 */
                                MiniPlayListAdapter.song = songList.get(holder.getAdapterPosition());
                                SongsFragment.dialog.show();
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
                /*Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putParcelableArrayListExtra("LIST", songList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                /*if (MyMediaPlayer.isPlayingSameSong()) { //prevents crash but causes progressbar to freak out sometimes
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }*/
                MainActivity ac = (MainActivity) context;
                ac.musicPlayer(songList);
                MainActivity.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        ImageView albumArtRImageView;
        ImageButton menuButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.song_title_recycler);
            albumArtRImageView = itemView.findViewById(R.id.playlist_icon_recycler);
            menuButton = itemView.findViewById(R.id.menu_button_recycler);
        }
    }
}

