package com.orbital.audinus;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment3 extends Fragment {

    private RecyclerView recyclerView;
    static ArrayList<AudioModel> songList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SearchView searchView;
    View rootView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            rootView = inflater.inflate(R.layout.fragment_fragment3, container, false);

            recyclerView = rootView.findViewById(R.id.recycler_view);
            TextView noMusicTextView = rootView.findViewById(R.id.no_songs_text);
            searchView = rootView.findViewById(R.id.search_bar);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ArrayList<AudioModel> filteredList = new ArrayList<>();
                    for (AudioModel x : songList) {
                        if (x.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(x);
                        }
                    }
                    recyclerView.setAdapter(new MusicListAdapter(filteredList, getActivity()));
                    return false;
                }
            });


            layoutManager = new LinearLayoutManager(rootView.getContext());



            if (songList.isEmpty()) {
                noMusicTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new MusicListAdapter(songList, getActivity()));
            }



        return rootView;
    }
}