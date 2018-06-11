package com.edu.s1572691.songle.songle;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//Adapter for the Listview in AchievementPopup
public class AchievementsListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> achievementNames = new ArrayList<>();
    private ArrayList<String> achievementDescriptions = new ArrayList<>();
    private ArrayList<Boolean> isComplete = new ArrayList<>();
    private Context context;

    AchievementsListAdapter(ArrayList<String> achievementNames, ArrayList<String> achievementDescriptions, ArrayList<Boolean> isComplete, Context context) {
        this.achievementNames = achievementNames;
        this.isComplete = isComplete;
        this.achievementDescriptions = achievementDescriptions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return achievementNames.size();
    }

    @Override
    public Object getItem(int position) {
        return achievementNames.get(position);
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
            if (inflater != null) {
                view = inflater.inflate(R.layout.achievementlistrow,null);
            }
        }
        assert view != null;
        TextView achievementTitle = (TextView) view.findViewById(R.id.achievementName);
        TextView achievementDescription = (TextView) view.findViewById(R.id.achievementDescription);
        ImageView achievementIcon = (ImageView) view.findViewById(R.id.achievementCircle);
        RelativeLayout achievementBG = (RelativeLayout) view.findViewById(R.id.achievementBG);

        achievementTitle.setText(achievementNames.get(position));
        achievementDescription.setText(achievementDescriptions.get(position));
        //Show green circle if completed else red circle
        if (isComplete.get(position)) {
            achievementIcon.setImageResource(R.drawable.circle_green);
        }
        else {
            achievementIcon.setImageResource(R.drawable.circle_red);
        }
        achievementBG.getBackground().setAlpha(255);





        return view;
    }
}
