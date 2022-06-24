package com.orbital.audinus;

import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    static final ArrayList<AudioModel> songList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SearchView searchView;
    View rootView;
    static Dialog dialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {

            dialog = new Dialog(this.getContext());
            dialog.setContentView(R.layout.playlist_selection);
            TextView back = dialog.findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            layoutManager = new LinearLayoutManager(dialog.getContext());
            RecyclerView recyclerView2 = dialog.findViewById(R.id.recycler_view);
            recyclerView2.setLayoutManager(layoutManager);
            recyclerView2.setAdapter(new MiniPlayListAdapter(PlaylistsFragment.playlists, getActivity(), PlaylistsFragment.nameList));

            rootView = inflater.inflate(R.layout.fragment_songs, container, false);

            recyclerView = rootView.findViewById(R.id.recycler_view);
            TextView noMusicTextView = rootView.findViewById(R.id.no_songs_text);
            searchView = rootView.findViewById(R.id.search_bar);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                        /*ArrayList<AudioModel> filteredList = new ArrayList<>();
                        for (AudioModel x : songList) {
                            if (x.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                filteredList.add(x);
                            }
                        }
                        recyclerView.setAdapter(new MusicListAdapter(filteredList, getActivity()));*/

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


            int searchCloseButtonId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_close_btn", null, null);
            ImageView closeButton = searchView.findViewById(searchCloseButtonId);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    recyclerView.setAdapter(new MusicListAdapter(songList, getActivity()));
                }
            });

            layoutManager = new LinearLayoutManager(rootView.getContext());


            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.DURATION,
                    MediaStore.Audio.AudioColumns.ALBUM_ID};


            Cursor cursor = requireActivity().getContentResolver().query(uri,
                    projection, null, null, null);


            while (cursor.moveToNext()) {
                long albumID = cursor.getLong(3);
                String albumArt = "content://media/external/audio/albumart/" + albumID;

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

        }
            return rootView;
    }

    static AudioModel getAudioModel(String name) {
        return songList.stream().filter(x -> Objects.equals(x.getTitle(), name)).findFirst().get();
    }
}