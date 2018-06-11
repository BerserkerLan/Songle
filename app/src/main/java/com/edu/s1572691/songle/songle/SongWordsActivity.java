package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class SongWordsActivity extends AppCompatActivity {

    TextView wordText;
    String no;
    String songTitle;
    String author;
    String lyrics;
    String youtube;
    String[] lyricsWords;
    String wordsWithDashes;
    long countOfWordsCollected;
    int percentageOfWords;
    SQLiteDatabase wordsDatabase;
    MediaPlayer songInBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_words);
        wordText = (TextView) findViewById(R.id.wordTextView);
        no = getIntent().getExtras().getString("num");
        songTitle = getIntent().getExtras().getString("title");
        author = getIntent().getExtras().getString("artist");
        youtube = getIntent().getExtras().getString("youtube");
        percentageOfWords = getIntent().getExtras().getInt("percentageOfWordsCollected");
        setTitle("Song " + no);
        wordText.getBackground().setAlpha(94);
        countOfWordsCollected = 0;
        new LyricsTask().execute();
        playMusic();
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
        lyricsWords = lyrics.split("\\n");
        ArrayList<String> wordsFound = new ArrayList<>();
        wordsDatabase = openOrCreateDatabase("Words", Context.MODE_PRIVATE, null);
        wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + songNo + "(wposs VARCHAR)");
        Cursor wordsCursor = wordsDatabase.rawQuery("SELECT * FROM tab" + songNo, null);
        wordsCursor.moveToFirst();
        String w = "";
        countOfWordsCollected = DatabaseUtils.longForQuery(wordsDatabase, "SELECT COUNT(*) FROM tab" + songNo, null);
        if (wordsCursor != null) {
            if (countOfWordsCollected > 0) {
                w = wordsCursor.getString(wordsCursor.getColumnIndex("wposs"));
                wordsFound.add(w);
            }
            while (wordsCursor.moveToNext()) {
                w = wordsCursor.getString(wordsCursor.getColumnIndex("wposs"));
                wordsFound.add(w);
            }
        }
        wordsWithDashes = "";

        String[] words;

        int numberOfWordsInSong = 0;

      //HERE
        for (int i = 0; i < lyricsWords.length; i++) {
            words = lyricsWords[i].split(" ");
            numberOfWordsInSong += words.length;
            for (int j = 0; j < words.length; j++) {
                if (wordsFound.contains((i + 1) + ":" + (j + 1))) {
                    wordsWithDashes += words[j];
                    wordsWithDashes += " ";
                }
                else {
                    for (int k = 0; k < words[j].length(); k++) {
                        wordsWithDashes += "_";
                    }
                    wordsWithDashes += " ";
                }
            }
            wordsWithDashes += "\n";
        }


        if (countOfWordsCollected == numberOfWordsInSong) {
            SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("ach6",1);
            editor.apply();
        }


    }

    public void playMusic() {
        songInBackground = songInBackground.create(getApplicationContext(),R.raw.songle_home_music);

        songInBackground.start();

        songInBackground.setLooping(true);
    }

    //Override methods as music was still playing when App was minimized........
    @Override
    protected void onPause() {
        if (songInBackground.isPlaying()) {
            songInBackground.pause();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        songInBackground.start();
        songInBackground.setLooping(true);
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



    public void popUpTheGuess(View v) {
        Intent intent = new Intent(this, GuessPopup.class);
        intent.putExtra("title",songTitle);
        intent.putExtra("artist",author);
        intent.putExtra("num",no);
        intent.putExtra("youtube",youtube);
        intent.putExtra("numCollected",countOfWordsCollected);
        intent.putExtra("percentageCollected",percentageOfWords);
        startActivity(intent);
    }
    public void showHint(View v) {
        Intent intent = new Intent(this, HintPopup.class);
        intent.putExtra("artistName",author);
        intent.putExtra("title",songTitle);
        intent.putExtra("firstLine",lyricsWords[0]);
        startActivity(intent);


    }
    //In case internet stops midway, go back to the menu if no internet
    @Override
    public void onBackPressed() {
        if (hasInternet()) {
            Intent intent = new Intent(this, SongList.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
            noInternetToast();
            finish();
        }
    }

    private class LyricsTask extends AsyncTask<Void,String,String> {

        @Override
        protected String doInBackground(Void... voids) {

            parseLyrics(no);

            return lyrics;

        }
        @Override
        protected void onPostExecute(String lyrics) {
            wordText.setText(wordsWithDashes);

        }

    }
}
