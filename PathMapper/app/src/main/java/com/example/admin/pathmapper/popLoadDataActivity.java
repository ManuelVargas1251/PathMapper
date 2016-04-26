package com.example.admin.pathmapper;

import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class popLoadDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_load_data);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.widthPixels;
        final Timer timer = new Timer();

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        MapController.getInstance().LoadData(this);

        //Timer loops checking if data has been completely loaded.
        timer.schedule(new TimerTask() {
            public void run() {
                if(MapController.getInstance().IsAllDataLoaded()) {
                    timer.cancel();
                    finish();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onBackPressed() {
    }
}

