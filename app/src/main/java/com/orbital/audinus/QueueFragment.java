package com.orbital.audinus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Queue;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link QueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueueFragment extends Fragment {

    private RecyclerView recyclerView;
    static ArrayList<AudioModel> songList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SearchView searchView;
    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            rootView = inflater.inflate(R.layout.fragment_queue, container, false);

            recyclerView = rootView.findViewById(R.id.inside_recycler_view);
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
                    recyclerView.setAdapter(new QueueAdapter(filteredList, getActivity()));
                    return false;
                }
            });

        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                recyclerView.setAdapter(new QueueAdapter(songList, getActivity()));
            }
        });


            layoutManager = new LinearLayoutManager(rootView.getContext());

            if (songList.isEmpty()) {
                noMusicTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new QueueAdapter(songList, getActivity()));
            }
        return rootView;
    }
}