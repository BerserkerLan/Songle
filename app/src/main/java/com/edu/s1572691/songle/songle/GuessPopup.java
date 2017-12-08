package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.awt.font.TextAttribute;


public class GuessPopup extends Activity {
    String songName;
    String songYoutube;
    String songNo;
    String songArist;
    TextView GuessText;
    RelativeLayout guessBG;
    int percentageCollected;
    long numberOfWordsCollected;
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
        numberOfWordsCollected = getIntent().getExtras().getLong("numCollected");
        percentageCollected = getIntent().getExtras().getInt("percentageCollected");

        GuessText = (TextView) findViewById(R.id.enterGuessText);
        GuessButton = (Button) findViewById(R.id.guessButton);
        guessBG = (RelativeLayout) findViewById(R.id.guessBg);

        guessBG.getBackground().setAlpha(94);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //Used to scale the activity screen
        getWindow().setLayout((int)(width*0.8),(int) (height * 0.6));
    }
    public void onGuessButtonClick(View v) {
        if (GuessText.getText() != null) {
            //If user guesses correctly, stored in a DB
            if (GuessText.getText().toString().contains(songName)) {
                Toast.makeText(getApplicationContext(),"CONGRATULATIONS! YOU HAVE GUESSED THE SONG",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,GuessedSongWords.class);
                intent.putExtra("title",songName);
                intent.putExtra("num",songNo);
                intent.putExtra("artist",songArist);
                intent.putExtra("youtube",songYoutube);
                guessedSongs = openOrCreateDatabase("GuessedSongs",Context.MODE_PRIVATE,null);
                guessedSongs.execSQL("CREATE TABLE IF NOT EXISTS guessedSongs(songNo VARCHAR)"); //DB contains all guesed songs
                guessedSongs.execSQL("INSERT INTO guessedSongs(songNo) VALUES('" + songNo + "');");
                guessedSongs.close();
                if (numberOfWordsCollected == 0) { //Check for obtaining achievement 1
                    SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ach5",1);
                    editor.apply();
                }
                if (percentageCollected == 1) { //Check for obtaining achievement 2, 1 if obtained, 0 if not
                    SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ach2",1);
                    editor.apply();
                }
                if (!usedHint()) { //Check for achievement 3
                    SharedPreferences settings = getSharedPreferences("Achievements",MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("ach3",1);
                    editor.apply();
                }
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Tough luck! Try again!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Checks whether user uses hint, used for Achievement 2 check
    public boolean usedHint() {
        SharedPreferences settings = getSharedPreferences("usedHints",MODE_PRIVATE);
        int used = settings.getInt("title:" + songName,0);
        return used == 1;
    }
}
