package com.orbital.audinus;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private final ArrayList<AudioModel> songList = new ArrayList<>();
    private ArrayList<AudioModel> searchList;
    private LinearLayoutManager layoutManager;
    private SearchView searchView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

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
                for (AudioModel x : songList){
                    if (x.getTitle().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(x);
                    }
                }
                recyclerView.setAdapter(new MusicListAdapter(filteredList, getActivity()));
                return false;
            }
        });



        layoutManager = new LinearLayoutManager(rootView.getContext());


        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID};


        /*String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Albums._ID
        };*/
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";


        Cursor cursor = requireActivity().getContentResolver().query(uri,
                projection, null, null, null);


        while (cursor.moveToNext()) {
            long albumID = cursor.getLong(3);
            String albumArt = "content://media/external/audio/albumart/"+ albumID;

            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2), albumArt);

            if (new File(songData.getPath()).exists()) {
                songList.add(songData);
            }

            if (songList.isEmpty()) {
                noMusicTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new MusicListAdapter(songList, getActivity()));
            }
        }
        cursor.close();



        return rootView;
    }
}