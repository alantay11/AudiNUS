package com.orbital.audinus;

import android.content.Context;


public class InsidePlaylistAdapter extends MusicListAdapter{
    public InsidePlaylistAdapter(int position, Context context) {
        super(PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(position)), context);
    }
}
