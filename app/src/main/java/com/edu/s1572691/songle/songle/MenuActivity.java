package com.edu.s1572691.songle.songle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

        final Intent toMaps = new Intent(this, MapsActivity.class);
        final Intent toSongs = new Intent(this, SongList.class);
        final Intent showTutorial = new Intent(this, TutorialPopup.class);
        final Intent showAch = new Intent(this, AchievementPopup.class);

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


    }



    private void requestPermission() {
//Function to request permission
        try {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        } catch (SecurityException e) {
    /*Catch any Security Exceptions*/
        }
    }
    public boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }
    public void noInternetToast() {
        Toast.makeText(getApplicationContext(), "Please connect to the internet to play the game",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onBackPressed() {
    }
}
