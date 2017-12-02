package com.edu.s1572691.songle.songle;

import android.content.Context;
import android.content.Intent;
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
    String author;
    String lyrics;
    String[] lyricsWords;
    String wordsWithDashes;
    SQLiteDatabase wordsDatabase;


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
        lyricsWords = lyrics.split("\\n");
        ArrayList<String> wordsFound = new ArrayList<>();
        wordsDatabase = openOrCreateDatabase("Words", Context.MODE_PRIVATE, null);
        wordsDatabase.execSQL("CREATE TABLE IF NOT EXISTS tab" + songNo + "(wposs VARCHAR)");
        Cursor wordsCursor = wordsDatabase.rawQuery("SELECT * FROM tab" + songNo, null);
        wordsCursor.moveToFirst();
        String w = "";
        long count = DatabaseUtils.longForQuery(wordsDatabase, "SELECT COUNT(*) FROM tab" + songNo, null);
        if (wordsCursor != null) {
            if (count > 0) {
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

        for (int i = 0; i < lyricsWords.length; i++) {
            words = lyricsWords[i].split(" ");
            for (int g=0; g < words.length; g++) {
                for (int k = 0; k < wordsFound.size(); k++) {
                    if (wordsFound.get(k).substring(0, wordsFound.get(k).indexOf(':')).equals((i + 1) + "") && (wordsFound.get(k).substring(wordsFound.get(k).indexOf(':') + 1)).equals((g+1) + "")) {
                        wordsWithDashes += words[Integer.parseInt(wordsFound.get(k).substring(wordsFound.get(k).indexOf(':') + 1)) - 1];
                        wordsWithDashes += ' ';
                    }
                    }
                for (int f = 0; f < words[g].length(); f++) {
                    wordsWithDashes += "_";
                }
                wordsWithDashes += ' ';
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
