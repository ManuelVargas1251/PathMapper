package com.example.admin.pathmapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class popsubmitConfirmActivity extends AppCompatActivity {

    private Button yesPopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_confirm);

        yesPopButton = (Button) findViewById(R.id.yesPopButton);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.widthPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        yesPopButtonClick();

      /*  //Closes window after 7 seconds if ok is not pressed.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                finish();
            }
        }, 7000);
      */
    }

    public void yesPopButtonClick() {
        yesPopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

