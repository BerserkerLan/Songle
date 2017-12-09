package com.edu.s1572691.songle.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// The Homepage Activity, that starts up when you start the app

public class MenuActivity extends AppCompatActivity {

    Button startMaps;
    Button goToSongs;
    Button showTut;
    Button showAchievements;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startMaps = (Button) findViewById(R.id.goToMap);
        goToSongs = (Button) findViewById(R.id.toSongs);
        showTut = (Button) findViewById(R.id.tutorialButton);
        showAchievements = (Button) findViewById(R.id.achievements);

        final Intent toMaps = new Intent(this, MapsActivity.class);
        final Intent toSongs = new Intent(this, SongList.class);
        final Intent showTutorial = new Intent(this, TutorialPopup.class);
        final Intent showAch = new Intent(this, AchievementPopup.class);

        //Ask for permission if not granted
        if (!(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        }


        startMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInternet()) {
                    noInternetToast();
                }
                else {
                    startActivity(toMaps);
                    finish();
                }
            }
        });
        goToSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInternet()) {
                    noInternetToast();
                } else {
                    startActivity(toSongs);
                    finish();
                }
            }
        });
        showTut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInternet()) {
                    noInternetToast();
                }
                else {
                    startActivity(showTutorial);
                }
            }
        });
        showAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInternet()) {
                    noInternetToast();
                }
                else {
                    startActivity(showAch);
                }
            }
        });

        //Plays Home screen music
        playMusic();


    }


    //Requests permission to use GPS/Location
    private void requestPermission() {
        try {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    //Checks if the user has Internet
    public boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }
    //Toast message to display in case of no Internet
    public void noInternetToast() {
        Toast.makeText(getApplicationContext(), "Please connect to the internet to play the game",Toast.LENGTH_LONG).show();
    }

    //Copyright free music from youtube, see documentation for credit !
    //In case I forget: https://www.youtube.com/watch?v=GoPfCriKrcY
    public void playMusic() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.songle_home_music);

        mediaPlayer.start();

        mediaPlayer.setLooping(true);
    }

    //Override methods as music was still playing when App was minimized........
    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }



    //So the application does not go to any previously opened activity that could lead to some bugs
    @Override
    public void onBackPressed() {
        finish();
    }
}
