package com.example.admin.pathmapper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class popCreatePath extends AppCompatActivity {

    private int nextActivityStatus;
    private String destString;

    private Button yesPopButton, noPopButton;
    private TextView popText;
    private LinearLayout layout;
    private EditText input;

    Intent returnIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_create_path);

        Intent intent = getIntent();
        nextActivityStatus = intent.getIntExtra("nextActivity", 0);

        yesPopButton = (Button) findViewById(R.id.yesPopButton);
        noPopButton = (Button) findViewById(R.id.noPopButton);
        popText = (TextView) findViewById(R.id.popWindowText);

        if(nextActivityStatus == 0) {
            layout = (LinearLayout) findViewById(R.id.layoutForInput);
            input = new EditText(popCreatePath.this);
            input.setHint("Enter Destination Here");
            input.setBackgroundColor(Color.WHITE);
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            input.setSingleLine();
            layout.addView(input);
            popText.setText("Please enter the destination name:\n");
            yesPopButton.setText("Submit");
            noPopButton.setText("Quit");
        }
        else if(nextActivityStatus == 1)
            popText.setText("Are you sure you wish to quit path creation?");

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
                destString = input.getText().toString();

                if(destString.length() > 0) {
                    returnIntent.putExtra("answer", 1);
                    returnIntent.putExtra("destString", destString);
                    setResult(popNextActivity.RESULT_OK, returnIntent);
                    finish();
                }
                else
                    input.setHint("Cannot leave blank...");
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
}