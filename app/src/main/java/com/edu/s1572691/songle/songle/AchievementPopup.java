package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class AchievementPopup extends Activity {

    ListView listOfAchievements;
    TextView numberAchieved;
    int achieved;
    SQLiteDatabase achievementsDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_achievement);

        listOfAchievements = (ListView) findViewById(R.id.listOfAchievements);
        numberAchieved = (TextView) findViewById(R.id.numAchieved);
        achieved = 0;


        setUpAchievementListview();
        

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));
    }
    public void setUpAchievementListview() {
        ArrayList<String> achievementTitles = new ArrayList<>();
        ArrayList<String> achievementDescriptions = new ArrayList<>();
        ArrayList<Boolean> isComplete = new ArrayList<>();

        achievementTitles.add("No Shit Sherlock");
        achievementTitles.add("Eager Walker");
        achievementTitles.add("Guessing Pro");
        achievementTitles.add("Dominator");
        achievementTitles.add("Unbeatable");
        achievementTitles.add("Master Collector");

        achievementDescriptions.add("Guess 3 Songs.");
        achievementDescriptions.add("Guess a song with only collecting words of one level.");
        achievementDescriptions.add("Guess a songs without using the hint.");
        achievementDescriptions.add("Guess 10 songs.");
        achievementDescriptions.add("Guess a song without collecting any words.");
        achievementDescriptions.add("Collect all words of any song.");



        for (int i = 0; i < achievementTitles.size(); i++) {
                isComplete.add(false);

        }
        SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);

        for (int i = 1; i <=6; i++ ) {
            if (settings.getInt(("ach" + i),0) == 1) {
                achieved ++;
                isComplete.set((i-1),true);
            }
        }
        numberAchieved.setText(achieved + "/6");
        AchievementsListAdapter achievementsListAdapter = new AchievementsListAdapter(achievementTitles,achievementDescriptions,isComplete,this);

        listOfAchievements.setAdapter(achievementsListAdapter);







    }
}
