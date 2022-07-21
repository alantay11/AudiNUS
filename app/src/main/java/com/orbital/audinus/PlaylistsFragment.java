package com.orbital.audinus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFragment extends Fragment {


    static RecyclerView recyclerView;
    static ArrayList<String> nameList;
    private LinearLayoutManager layoutManager;
    private static final String FILE_NAME = "example.txt";
    EditText mEditText;
    static HashMap<String, ArrayList<AudioModel>> playlists;
    private TextView noPlaylistTextView;
    PlayListAdapter adapter;
    static int position;
    static Dialog dialog;
    static ImageButton createPlayList;
    static boolean read;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.create_playlist);
        mEditText = dialog.findViewById(R.id.edit_text_input);
        Button saveButton = dialog.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> {
            save(getView());
            dialog.dismiss();
        });

        Button cancelButton = dialog.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            mEditText.getText().clear();
        });


        createPlayList = rootView.findViewById(R.id.createPlaylist);
        createPlayList.setOnClickListener(v -> dialog.show());

        recyclerView = rootView.findViewById(R.id.inside_recycler_view);
        layoutManager = new LinearLayoutManager(rootView.getContext());

        noPlaylistTextView = rootView.findViewById(R.id.no_playlists_text);


        //TextView loadButton = rootView.findViewById(R.id.button_load);
        //loadButton.setOnClickListener(v -> load(this.getView()));
/*
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
                playlists.put(title,songTitles);
                nameList.add(title);
            }
        }


        if (playlists.isEmpty()) {
            String fav = "Favourites";
            playlists.put(fav, new ArrayList<>());
            nameList.add(fav);
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

 */
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlayListAdapter(playlists, getActivity(), nameList);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;

        if (text.length() == 0 || playlists.containsKey(text)){
            Toast.makeText(getContext(), "Playlist already exists or is invalid", Toast.LENGTH_SHORT).show();
        } else {
            playlists.put(text, new ArrayList<>());
            nameList.add(text);
            adapter.notifyItemInserted(nameList.size());
        }


        try {
            StringBuilder x = new StringBuilder();
            for(String y : nameList) {
                x.append(y).append("!@#");
                for (AudioModel z : Objects.requireNonNull(playlists.get(y))) {
                    x.append(z.getTitle()).append(";;;");
                }
                x.append("\n");
                noPlaylistTextView.setVisibility(View.INVISIBLE);
            }

            fos = requireActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(x.toString().getBytes());
            mEditText.getText().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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