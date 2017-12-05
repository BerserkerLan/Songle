package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.awt.font.TextAttribute;

/**
 * Created by Rusab Asher on 30/10/2017.
 */

public class GuessPopup extends Activity {
    String songName;
    String songYoutube;
    String songNo;
    String songArist;
    TextView GuessText;
    Button GuessButton;
    SQLiteDatabase guessedSongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_guess);
        songName = getIntent().getExtras().getString("title");
        songNo = getIntent().getExtras().getString("num");
        songArist = getIntent().getExtras().getString("artist");
        songYoutube = getIntent().getExtras().getString("youtube");
        GuessText = (TextView) findViewById(R.id.enterGuessText);
        GuessButton = (Button) findViewById(R.id.guessButton);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int) (height * 0.6));
    }
    public void onGuessButtonClick(View v) {
        if (GuessText.getText() != null) {
            if (GuessText.getText().toString().contains(songName)) {
                Toast.makeText(getApplicationContext(),"CONGRATULATIONS! YOU HAVE GUESSED THE SONG",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,GuessedSongWords.class);
                intent.putExtra("title",songName);
                intent.putExtra("num",songNo);
                intent.putExtra("artist",songArist);
                intent.putExtra("youtube",songYoutube);
                guessedSongs = openOrCreateDatabase("GuessedSongs",Context.MODE_PRIVATE,null);
                guessedSongs.execSQL("CREATE TABLE IF NOT EXISTS guessedSongs(songNo VARCHAR)");
                guessedSongs.execSQL("INSERT INTO guessedSongs(songNo) VALUES('" + songNo + "');");
                guessedSongs.close();
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Tough luck! Try again!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
