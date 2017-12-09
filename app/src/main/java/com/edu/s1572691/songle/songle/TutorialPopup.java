package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.EditText;

//Class to just show the tutorial/ How to Play

public class TutorialPopup extends Activity {

    EditText tutText;
    MediaPlayer songInBackground;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_tutorial);
        tutText = (EditText) findViewById(R.id.tutorialText);

        tutText.setText("Collect words from the campus for each song, to unlock words for that Song. Select the Song number by clicking the number while playing. Words are saved, and can be viewed in your List of songs. You can guess the song any time by pressing the \'Guess\' Button. You can also select which level you would like to play");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        playMusic();

        getWindow().setLayout((int)(width*0.8),(int) (height * 0.6));

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
}
