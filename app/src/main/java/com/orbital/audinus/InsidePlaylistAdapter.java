package com.orbital.audinus;

import android.content.Context;

import java.util.ArrayList;

public class InsidePlaylistAdapter extends MusicListAdapter{
    public InsidePlaylistAdapter(int position, Context context) {
        super(PlaylistsFragment.playlists.get(PlaylistsFragment.nameList.get(position)), context);
    }
}
