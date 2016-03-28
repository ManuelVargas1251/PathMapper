package com.example.admin.gpstracker;

//Standard and Content Imports
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

public class MainActivity extends AppCompatActivity{

    private Button searchButton, clearButton;
    private EditText searchInput;
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.searchButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        searchInput = (EditText) findViewById(R.id.searchInput);

        searchButtonClick();
        clearButtonClick();
    }

    public void searchButtonClick(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchString = searchInput.getText().toString();

                if (searchLocExist(searchString))
                    startActivity(new Intent(getApplicationContext(), createPathActivity.class));
                else {
                    searchInput.setText("");
                    startActivity(new Intent(getApplicationContext(), createPathPopActivity.class));
                }
            }
        });
    }

    public void clearButtonClick(){
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
            }
        });
    }

    public boolean searchLocExist(String searchedLoc){
        if(searchedLoc.length() >  2)
            return true;

        return false;
    }

}
