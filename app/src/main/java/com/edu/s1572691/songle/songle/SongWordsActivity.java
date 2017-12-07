package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
    long countOfWordsCollected = 0;
    int percentageOfWords;
    SQLiteDatabase wordsDatabase;


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
        new LyricsTask().execute();
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,SongList.class);
        startActivity(intent);
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
