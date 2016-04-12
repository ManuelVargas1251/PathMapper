package com.example.admin.pathmapper;

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
    private int answer, searchFlag;

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
                Intent intent = new Intent(getApplicationContext(), popNextActivity.class);
                //intent.putExtra("searchString", searchString);

                searchFlag = searchLocExist(searchString);

                if(searchFlag == 0){
                    intent.putExtra("nextActivity", 0);
                    startActivityForResult(intent, 1);
                }
                else if (searchFlag == 1) {
                    intent.putExtra("nextActivity", 1);
                    startActivityForResult(intent, 1);
                }

                searchInput.setText("");
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

    public int searchLocExist(String searchedLoc){

        if(searchedLoc.length() > 0)
            return 1;
        else
            return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){
            if(resultCode == popNextActivity.RESULT_OK){
                answer = data.getIntExtra("answer", 0);

                //Passes Search String to Create Path Activity.
                if(searchFlag == 0 && answer == 1) {
                    Intent nextActivityIntent = new Intent(getApplicationContext(), createPathActivity.class);
                    nextActivityIntent.putExtra("searchString", searchString);
                    startActivity(nextActivityIntent);
                }
                //Passes Search String to Navigate Path Activity.
                else if(searchFlag == 1 && answer == 1) {
                    Intent nextActivityIntent = new Intent(getApplicationContext(), navigateActivity.class);
                    nextActivityIntent.putExtra("searchString", searchString);
                    startActivity(nextActivityIntent);
                }
            }
        }
    }
}
