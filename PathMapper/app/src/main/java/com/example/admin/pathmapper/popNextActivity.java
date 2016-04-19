package com.example.admin.pathmapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class popNextActivity extends AppCompatActivity {

    private int nextActivityStatus;
    private String searchString;

    private Button yesPopButton, noPopButton;
    private TextView popText;

    Intent returnIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_next);

        Intent intent = getIntent();
        searchString = intent.getStringExtra("searchString");
        nextActivityStatus = intent.getIntExtra("nextActivity", 0);

        yesPopButton = (Button) findViewById(R.id.yesPopButton);
        noPopButton = (Button) findViewById(R.id.noPopButton);
        popText = (TextView) findViewById(R.id.popWindowText);

        if(nextActivityStatus == 0)
            popText.setText("Path Mapper could not find a path near your searched location, would you like to create a new path?");
        else if(nextActivityStatus == 1)
            popText.setText("Path Mapper found a path near your searched location, would you like to navigate that path?");

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
                returnIntent.putExtra("answer", 1);
                setResult(popNextActivity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    public void noPopButtonClick(){
        noPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnIntent.putExtra("answer", 0);
                setResult(popNextActivity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
