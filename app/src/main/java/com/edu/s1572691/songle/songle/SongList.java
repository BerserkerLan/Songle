package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SongList extends AppCompatActivity {

    ListView songList;
    ArrayList<String> songs;
    ArrayList<String> authors;
    ArrayList<String> titles;
    ArrayList<String> percents;
    String songURL;

    CustomSongAdapter songAdapter;
    int numOfWordsInSong;
    ArrayList<String> youtubeURLS;
    SongParse theSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Songs");
        songList = (ListView) findViewById(R.id.songList);
        songs = new ArrayList<>();
        authors = new ArrayList<>();
        percents = new ArrayList<>();
        titles = new ArrayList<>();
        youtubeURLS = new ArrayList<>();

        songURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.txt";

        new XMLTask().execute();


    }
    //Overwritten to not allow any activity overlaps
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
        finish();
    }

    //Method to set up Listview
    public void parseXML()  {
        URL oracle = null;
        InputStreamReader in = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            oracle = new URL(songURL);
            in = new InputStreamReader(oracle.openStream());
            reader = new BufferedReader(in);

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String xml = stringBuilder.toString();
        JSONObject jsonObject = null;
        try {
            jsonObject = XML.toJSONObject(xml);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonObject != null;
        String json = jsonObject.toString();
        Gson gson = new Gson();
        theSongs = gson.fromJson(json, SongParse.class);


        for (int i = 0; i < theSongs.getSong().getSong().length; i++) {

            authors.add(theSongs.getSong().getSong()[i].getArtist());
            youtubeURLS.add(theSongs.getSong().getSong()[i].getLink());
            titles.add(theSongs.getSong().getSong()[i].getTitle());
            if (i < 9) {
                songs.add("0" + (i + 1));
            }
            else {
                songs.add("" + (i + 1));
            }
        }
       SQLiteDatabase wordsDatabase = openOrCreateDatabase("Words", Context.MODE_PRIVATE,null);
        DecimalFormat df = new DecimalFormat("#.#");

        //Get the songs which have been guessed from the Database
        SQLiteDatabase guessedSongs = openOrCreateDatabase("GuessedSongs",Context.MODE_PRIVATE,null);
        guessedSongs.execSQL("CREATE TABLE IF NOT EXISTS guessedSongs(songNo VARCHAR)");
        Cursor resultWords = guessedSongs.rawQuery("SELECT * FROM guessedSongs",null);
        resultWords.moveToFirst();
        String word;
        ArrayList<String> guessedWords = new ArrayList<>();
        guessedWords.clear();
        long countForWords = DatabaseUtils.longForQuery(guessedSongs, "SELECT COUNT(*) FROM guessedSongs",null);
        if (countForWords>0) {
            word = resultWords.getString(resultWords.getColumnIndex("songNo"));
            guessedWords.add(word);
        }
        while (resultWords.moveToNext()) {
            word = resultWords.getString(resultWords.getColumnIndex("songNo"));
            guessedWords.add(word);
        }
        guessedSongs.close();
        resultWords.close();

        //If songs have been guessed, just add 100% complete, else add the words found of it as a percentage
        for (String sg : songs) {
            if (!guessedWords.contains(sg)) {
                getNumWordsInSongs(sg.substring(sg.indexOf(' ') + 1));
                wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + sg.substring(sg.indexOf(' ') + 1) + "(wposs VARCHAR)");
                long count = DatabaseUtils.longForQuery(wordsDatabase, "SELECT COUNT(*) FROM tab" + sg.substring(sg.indexOf(' ') + 1), null);
                percents.add(df.format(((double) count / (double) numOfWordsInSong) * (double) 100d) + "% words found");
            }
            else {
                percents.add("100% words found");
            }
        }
        int solvedCount = 0;

        for (int i = 0; i < percents.size(); i++) {
            if (percents.get(i) == "100% words found") {
                solvedCount++;
            }
        }
        //Check the number of solved songs for the Achievements
        SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (solvedCount >= 3) {
            editor.putInt("ach1",1);
        }
        if (solvedCount >= 10) {
            editor.putInt("ach4",1);
        }

        editor.apply();

        songAdapter = new CustomSongAdapter(songs,percents,this);

    }
    //Returns the number of words each song has
    public void getNumWordsInSongs(String songNo) {
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
        String lyrics = stringBuilder.toString();

        String[] lyricsWords = lyrics.split("\\n");

        String[] words;

        numOfWordsInSong = 0;

        for (String lyricsWord : lyricsWords) {
            words = lyricsWord.split(" ");
            numOfWordsInSong += words.length;
        }
    }
    //Selects whether to go to guessed words activity or SongWordsActivity based on songs collected
    public void goToLyrics(View v) {
        if (!hasInternet()) {
            noInternetToast();
        }
        else {
            Intent intent = new Intent(this, SongWordsActivity.class);
            Intent intent2 = new Intent(this, GuessedSongWords.class);
            intent.putExtra("num", getSelected(v));
            intent.putExtra("title", getSelectedTitle(v));
            intent.putExtra("artist", getSelectedAuthor(v));
            intent.putExtra("youtube", getSelectedYoutube(v));
            intent.putExtra("percentageOfWordsCollected", getSelectedPercentage(v));

            intent2.putExtra("title", getSelectedTitle(v));
            intent2.putExtra("artist", getSelectedAuthor(v));
            intent2.putExtra("youtube", getSelectedYoutube(v));
            intent2.putExtra("num", getSelected(v));
            if (((TextView) v.findViewById(R.id.percentTextView)).getText().toString().equals("100% words found")) {
                startActivity(intent2);
                finish();
            } else {
                startActivity(intent);
                finish();
            }
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

    public String getSelectedTitle(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        return titles.get(n);
    }
    public String getSelected(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        sel = sel.substring(sel.length() - 2, sel.length());
        return sel;
    }
    public String getSelectedAuthor(View v) {
        String sgNameSelected =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sgNameSelected);
        return authors.get(n);

    }
    public int getSelectedPercentage(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        if (percents.get(n).equals("25% words found")) {
            return 1;
        }
        return 0;
    }
    public String getSelectedYoutube(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        return youtubeURLS.get(n);
    }

    //Async task to parse XML

    private class XMLTask extends AsyncTask<Void,String,SongParse> {

        @Override
        protected SongParse doInBackground(Void... voids) {
            parseXML();
            return theSongs;
        }
        @Override
        protected void onPostExecute(SongParse song) {
            songList.setAdapter(songAdapter);
        }

    }


}
