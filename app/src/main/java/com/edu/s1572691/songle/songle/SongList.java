package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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

        parseXML();


    }

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

        } catch (MalformedURLException e) {

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

        CustomSongAdapter songAdapter = new CustomSongAdapter(songs,percents,this);
        songList.setAdapter(songAdapter);
    }
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
                stringBuilder.append(line + "\n");
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String lyrics = stringBuilder.toString();

        String[] lyricsWords = lyrics.split("\\n");

        String[] words;

        numOfWordsInSong = 0;

        for (int i = 0; i < lyricsWords.length; i++) {
            words = lyricsWords[i].split(" ");
            numOfWordsInSong += words.length;
        }
    }
    public void goToLyrics(View v) {
        Intent intent = new Intent(this, SongWordsActivity.class);
        Intent intent2 = new Intent(this,GuessedSongWords.class);
        intent.putExtra("num",getSelected(v));
        intent.putExtra("title",getSelectedTitle(v));
        intent.putExtra("artist",getSelectedAuthor(v));
        intent.putExtra("youtube",getSelectedYoutube(v));

        intent2.putExtra("title",getSelectedTitle(v));
        intent2.putExtra("artist",getSelectedAuthor(v));
        intent2.putExtra("youtube",getSelectedYoutube(v));
        intent2.putExtra("num",getSelected(v));
        if (((TextView) v.findViewById(R.id.percentTextView)).getText().toString() == "100% words found") {
            startActivity(intent2);
        }
        else {
            startActivity(intent);
        }
    }

    public String getSelectedTitle(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        String titl = titles.get(n);
        return titl;
    }
    public String getSelected(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        sel = sel.substring(sel.length() - 2, sel.length());
        return sel;
    }
    public String getSelectedAuthor(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        String author = authors.get(n);
        return author;

    }
    public String getSelectedYoutube(View v) {
        String sel =  ((TextView) v.findViewById(R.id.songNameTextView)).getText().toString();
        int n = songs.indexOf(sel);
        String youtube = youtubeURLS.get(n);
        return youtube;
    }


}
