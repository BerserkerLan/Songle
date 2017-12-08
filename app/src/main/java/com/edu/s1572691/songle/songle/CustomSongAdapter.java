package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//Adapter for Listview of SongList
public class CustomSongAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> songNames = new ArrayList<>();
    private ArrayList<String> songPercents = new ArrayList<>();
    private Context context;

    public CustomSongAdapter(ArrayList<String> songNames, ArrayList<String> songPercents, Context context) {
        this.songNames = songNames;
        this.songPercents = songPercents;
        this.context = context;
    }
    @Override
    public int getCount() {
        return songNames.size();
    }

    @Override
    public Object getItem(int position) {
        return songNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.songlistrow,null);
        }

        TextView songName = (TextView) view.findViewById(R.id.songNameTextView);
        TextView percentText = (TextView) view.findViewById(R.id.percentTextView);
        songName.setText(songNames.get(position));
        percentText.setText(songPercents.get(position));
        return view;
    }
}
