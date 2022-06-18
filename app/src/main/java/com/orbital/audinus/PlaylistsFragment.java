package com.orbital.audinus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFragment extends Fragment {

    private RecyclerView recyclerView;
    //static ArrayList<Playlist> playlists = new ArrayList<>();
    private LinearLayoutManager layoutManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        TextView noPlaylistTextView = rootView.findViewById(R.id.no_playlists_text);

        /*if (playlists.isEmpty()) {
            */ noPlaylistTextView.setVisibility(View.VISIBLE); /*
        } else {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new MusicListAdapter(playlists, getActivity()));
        }*/




        return rootView;
    }
}