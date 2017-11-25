package com.edu.s1572691.songle.songle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button startMaps;
    Button goToSongs;
    Button showTut;
    Button showAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startMaps = (Button) findViewById(R.id.goToMap);
        goToSongs = (Button) findViewById(R.id.toSongs);
        showTut = (Button) findViewById(R.id.tutorialButton);
        showAchievements = (Button) findViewById(R.id.achievements);

        final Intent toMaps = new Intent(this,MapsActivity.class);
        final Intent toSongs = new Intent(this,SongList.class);
        final Intent showTutorial = new Intent(this,TutorialPopup.class);
        final Intent showAch = new Intent(this, AchievementPopup.class);

        startMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toMaps);
            }
        });
        goToSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toSongs);
            }
        });
        showTut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showTutorial);
            }
        });
        showAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showAch);
            }
        });



    }
}
