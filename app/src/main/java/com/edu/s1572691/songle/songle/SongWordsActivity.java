package com.edu.s1572691.songle.songle;

import android.content.Intent;
import android.os.AsyncTask;
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


public class SongWordsActivity extends AppCompatActivity {

    TextView wordText;
    String no;
    String author;
    String lyrics;
    String[] lyricsWords;
    String wordsWithDashes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_words);
        wordText = (TextView) findViewById(R.id.wordTextView);
        no = getIntent().getExtras().getString("selected");
        author = getIntent().getExtras().getString("selectedAuthor");
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
                stringBuilder.append(line + "\n");
            }
            in.close();

        } catch (MalformedURLException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

        lyrics = stringBuilder.toString();

        /*lyrics = lyrics.replaceAll(",","");
        lyrics = lyrics.replaceAll("\\?","");
        lyrics = lyrics.replaceAll("\\.","");
        lyrics = lyrics.replaceAll("\\)","");
        lyrics = lyrics.replaceAll("\\(","");*/

        lyricsWords = lyrics.split("\\n");



        wordsWithDashes = "";

        for (int i = 0; i < lyricsWords.length; i++) {
            for (int j = 0; j < lyricsWords[i].length(); j++) {
                if (lyricsWords[i].charAt(j) == ' ') {
                    wordsWithDashes += lyricsWords[i].charAt(j);
                }
                else {
                    wordsWithDashes += '_';
                }
            }
            wordsWithDashes += "\n";
        }
    }

    public void popUpTheGuess(View v) {
        startActivity(new Intent(SongWordsActivity.this, GuessPopup.class));
    }
    public void showHint(View v) {
        Intent intent = new Intent(this, HintPopup.class);
        intent.putExtra("artistName",author);
        intent.putExtra("firstLine",lyricsWords[0]);
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
