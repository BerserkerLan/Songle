package com.edu.s1572691.songle.songle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.TextView;


public class HintPopup extends Activity {

    TextView hint;
    TextView firstLineText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_hint);

        hint = (TextView) findViewById(R.id.hintTextView);
        firstLineText = (TextView) findViewById(R.id.firstLineTextView);

        String title = getIntent().getExtras().getString("title");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;



        //Store this song in the usedHints shared preference as needed for an Achievement
        SharedPreferences settings = getSharedPreferences("usedHints",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("title:" + title ,1);
        editor.apply();



        getWindow().setLayout((int)(width*0.8),(int) (height*0.5));

        String artist = getIntent().getExtras().getString("artistName");

        String firstLine = getIntent().getExtras().getString("firstLine");

        hint.setText("Artist name: " + artist);
        firstLineText.setText("First Line of song: \n" + firstLine);


    }
}
