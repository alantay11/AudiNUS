package com.orbital.audinus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    static ArrayList<AudioModel> songList;
    private LinearLayoutManager layoutManager;
    private SearchView searchView;
    View rootView;
    static Dialog dialog;
    static MiniPlayListAdapter miniPlayListAdapter;

    private static final String FILE_NAME = "example.txt";
    static boolean read;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {

            songList = requireArguments().getParcelableArrayList("SONGS");

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
            RecyclerView recyclerView2 = dialog.findViewById(R.id.inside_recycler_view);
            recyclerView2.setLayoutManager(layoutManager);
            Supplier<HashMap<String, ArrayList<AudioModel>>> x = () -> PlaylistsFragment.playlists;
            Supplier<ArrayList<String>> y = () -> PlaylistsFragment.nameList;
            miniPlayListAdapter = new MiniPlayListAdapter(x.get(), getActivity(), y.get());
            recyclerView2.setAdapter(miniPlayListAdapter);

            rootView = inflater.inflate(R.layout.fragment_songs, container, false);

            recyclerView = rootView.findViewById(R.id.inside_recycler_view);
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
                    searchView.onActionViewCollapsed();
                    recyclerView.setAdapter(new MusicListAdapter(songList, getActivity()));
                }
            });

            layoutManager = new LinearLayoutManager(rootView.getContext());


            /*Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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
                }*/

                if (songList.isEmpty()) {
                    noMusicTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(new MusicListAdapter(songList, getActivity()));
                }
            }
            //cursor.close();

        //}


        ArrayList<String> a = load(this.getView());

        if (!read){
            read = true;
            for (String x : a){
                ArrayList<AudioModel> songTitles = new ArrayList<>();
                int index = x.indexOf("!@#");
                String title = x.substring(0,index);
                x = x.substring(index + 3);
                while (x.length() > 0) {
                    index = x.indexOf(";;;");
                    String songName = x.substring(0,index);
                    x = x.substring(index + 3);
                    songTitles.add(SongsFragment.getAudioModel(songName));
                }
                PlaylistsFragment.playlists.put(title,songTitles);
                PlaylistsFragment.nameList.add(title);
            }

            if (PlaylistsFragment.playlists.isEmpty()) {
                String fav = "Favourites";
                PlaylistsFragment.playlists.put(fav, new ArrayList<>());
                PlaylistsFragment.nameList.add(fav);
                FileOutputStream fos;
                try {
                    fos = requireActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    fos.write((fav+"!@#").getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //noPlaylistTextView.setVisibility(View.VISIBLE);


            }
        }
            return rootView;
    }

    static AudioModel getAudioModel(String name) {
        return songList.stream().filter(x -> Objects.equals(x.getTitle(), name)).findFirst().get();
    }

    public ArrayList<String> load(View v) {
        FileInputStream fis = null;
        ArrayList<String> a = new ArrayList<>();

        try {
            fis = requireActivity().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                a.add(text);
                sb.append(text).append("\n");
            }
            //mEditText.setText(sb.toString());//for debugging, delete to not display anything on the input line

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return a;
    }
}