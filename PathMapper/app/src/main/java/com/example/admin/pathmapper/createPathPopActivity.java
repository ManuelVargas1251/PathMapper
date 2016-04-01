package com.example.admin.pathmapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.admin.pathmapper.R;

public class createPathPopActivity extends AppCompatActivity {

    private Button yesPopButton, noPopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_path_pop);

        yesPopButton = (Button) findViewById(R.id.yesPopButton);
        noPopButton = (Button) findViewById(R.id.noPopButton);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.widthPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .6));

        yesPopButtonClick();
        noPopButtonClick();

    }

    public void yesPopButtonClick(){
        yesPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), createPathActivity.class));
                finish();
            }
        });
    }

    public void noPopButtonClick(){
        noPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
