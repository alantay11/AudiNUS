package com.orbital.audinus;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
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



        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Albums._ID
        };
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";


        Cursor cursor = requireActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);


        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
            Bitmap albumArt = null;
            try {
                albumArt = getAlbumArtwork(requireActivity().getContentResolver(), id);
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    private Bitmap getAlbumArtwork(ContentResolver resolver, long albumId) throws IOException {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId);
        return resolver.loadThumbnail(contentUri, new Size(300, 300), null);
    }



}