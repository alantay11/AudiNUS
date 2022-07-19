package com.orbital.audinus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class insideplaylist1 extends Fragment {

    static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    static ImageButton back;
    static TextView playlistName;

    static boolean isShowing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_insideplaylist1, container, false);
        recyclerView = rootView.findViewById(R.id.inside_recycler_view);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new InsidePlaylistAdapter(PlaylistsFragment.position, this.getContext()));
        playlistName = rootView.findViewById(R.id.inside_playlist_name);
        playlistName.setText(PlaylistsFragment.nameList.get(PlaylistsFragment.position));
        back = rootView.findViewById(R.id.inside_playlist_back);
        back.setOnClickListener(v -> {
            Fragment fragment = new PlaylistsFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.inside_playlist, fragment);
            fragmentTransaction.commit();
            update();
        });
        return rootView;
    }

    public static void update(){
        back.setVisibility(View.INVISIBLE);
        playlistName.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        isShowing =false;
    }

}