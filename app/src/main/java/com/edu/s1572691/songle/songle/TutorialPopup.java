package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.EditText;

/**
 * Created by Rusab Asher on 02/11/2017.
 */

public class TutorialPopup extends Activity {

    EditText tutText;

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

        getWindow().setLayout((int)(width*0.8),(int) (height * 0.6));


    }
}
