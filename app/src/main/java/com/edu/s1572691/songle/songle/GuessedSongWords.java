package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GuessedSongWords extends YouTubeBaseActivity {

    TextView songTitle;
    TextView songArist;
    EditText songLyrics;
    YouTubePlayerView youTubePlayerView;
    String lyrics;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessed_song_words);
        songTitle = (TextView) findViewById(R.id.songTitle);
        songArist = (TextView) findViewById(R.id.artistName);
        songLyrics = (EditText) findViewById(R.id.songLyrics);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubePlayer);

        String title = getIntent().getExtras().getString("title");
        String artist = getIntent().getExtras().getString("artist");
        String no = getIntent().getExtras().getString("num");
        final String youtubeURLg = getIntent().getExtras().getString("youtube");
        //As the player only needs the youtube ID and not the whole URL
        final String youtubeURL = youtubeURLg.substring(youtubeURLg.lastIndexOf('/') + 1, youtubeURLg.length());

        setTitle(title);

        //Sets up the youtube video player
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youtubeURL);


            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        youTubePlayerView.initialize(PlayerConfig.API_KEY, onInitializedListener);

        //Loads the words for the song
        parseLyrics(no);

        songTitle.setText("Name: " +  title);
        songArist.setText("Artist: " + artist);
        songLyrics.setText(lyrics);


    }
    public void parseLyrics(String songNo) {
        String lyricURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + songNo + "/lyrics.txt";
        URL oracle = null;
        InputStreamReader in = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            oracle = new URL(lyricURL);
            in = new InputStreamReader(oracle.openStream());
            reader = new BufferedReader(in);

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        lyrics = stringBuilder.toString();

    }
    //Checks if the user has Internet
    public boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
    //Toast message to display in case of no Internet
    public void noInternetToast() {
        Toast.makeText(getApplicationContext(), "Please connect to the internet to play the game",Toast.LENGTH_LONG).show();
    }
    //Exit to menu if no internet
    @Override
    public void onBackPressed() {
        if (hasInternet()) {
            Intent intent = new Intent(this, SongList.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            noInternetToast();
            finish();
        }
    }

}
