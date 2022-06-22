package com.orbital.audinus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFragment extends Fragment {


    private RecyclerView recyclerView;
    static ArrayList<String> nameList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private static final String FILE_NAME = "example.txt";
    EditText mEditText;
    static HashMap<String, ArrayList<AudioModel>> playlists = new HashMap<>(); //!@#
    private TextView noPlaylistTextView;
    PlayListAdapter adapter;
    static int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playlists, container, false);


        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(rootView.getContext());

        noPlaylistTextView = rootView.findViewById(R.id.no_playlists_text);
        mEditText = rootView.findViewById(R.id.edit_text);
        TextView saveButton = rootView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> save(this.getView()));
        TextView loadButton = rootView.findViewById(R.id.button_load);
        loadButton.setOnClickListener(v -> load(this.getView()));
        ArrayList<String> a = load(this.getView());
        for (String x : a){
            ArrayList<AudioModel> songTitles = new ArrayList<>();
            int index = x.indexOf("!@#");
            String title = x.substring(0,index);
            x = x.substring(index + 3, x.length());
            while (x.length() > 0) {
                index = x.indexOf(";;;");
                String songName = x.substring(0,index);
                x = x.substring(index + 3, x.length());
                songTitles.add(SongsFragment.getAudioModel(songName));
            }
            playlists.put(title,songTitles);
            nameList.add(title);
        }



        if (playlists.isEmpty()) {
            String fav = "Favourites";
            playlists.put(fav, new ArrayList<>());
            nameList.add(fav);
            FileOutputStream fos = null;
            try {
                fos = getActivity().openFileOutput(FILE_NAME, getActivity().MODE_PRIVATE);
                fos.write((fav+"!@#").getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //noPlaylistTextView.setVisibility(View.VISIBLE);


        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlayListAdapter(playlists, getActivity(), nameList);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;
        playlists.put(text, new ArrayList<>());
        nameList.add(text);
        adapter.notifyItemInserted(nameList.size());




        try {
            String x = "";
            for(String y : nameList) {
                x = x + y + "!@#";
                for (AudioModel z : playlists.get(y)) {
                    x = x + z.getTitle() + ";;;";
                }
                x+= "\n";
                noPlaylistTextView.setVisibility(View.INVISIBLE);
            }

            fos = getActivity().openFileOutput(FILE_NAME, getActivity().MODE_PRIVATE);
            fos.write(x.getBytes());
            mEditText.getText().clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            fis = getActivity().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                a.add(text);
                sb.append(text).append("\n");
            }
            //mEditText.setText(sb.toString());//for debugging, delete to not display anything on the input line

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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