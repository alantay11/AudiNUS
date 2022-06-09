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
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment1#} factory method to
 * create an instance of this fragment.
 */
public class fragment1 extends Fragment {

    private RecyclerView recyclerView;
    private final ArrayList<AudioModel> songList = new ArrayList<>();
    private int lastPosition;
    private LinearLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment1, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        TextView noMusicTextView = rootView.findViewById(R.id.no_songs_text);

        layoutManager = new LinearLayoutManager(rootView.getContext());



        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Albums._ID
        };
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
            Bitmap albumArt = null;
            try {
                albumArt = getAlbumArtwork(getActivity().getContentResolver(), id);
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