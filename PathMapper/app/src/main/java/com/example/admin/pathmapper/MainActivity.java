package com.example.admin.pathmapper;

//Standard and Content Imports
import android.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    private Button searchButton, clearButton;
    private EditText searchInput;
    private TextView errorText;
    private String searchString;
    private int answer, searchFlag, searchLock = 0;
    private long timeoutTime = 10000;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng currentLoc = null ;

    final Timer timer = new Timer();
    CountDownTimer timeoutTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.searchButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        searchInput = (EditText) findViewById(R.id.searchInput);
        errorText = (TextView) findViewById(R.id.errorText);

        searchButtonClick();
        clearButtonClick();

        timer.schedule(new TimerTask() {
            public void run() {
                Intent loadDataIntent = new Intent(getApplicationContext(), popLoadDataActivity.class);
                loadDataIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loadDataIntent);
                timer.cancel();
            }
        }, 1000);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(locationListener);
                timeoutTimer.cancel();
                currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

                Intent intent = new Intent(getApplicationContext(), popNextActivity.class);
                //intent.putExtra("searchString", searchString);

                searchFlag = searchLocExist(searchString);

                if (searchFlag == 0) {
                    intent.putExtra("nextActivity", 0);
                    startActivityForResult(intent, 2);
                } else if (searchFlag == 1) {
                    intent.putExtra("nextActivity", 1);
                    startActivityForResult(intent, 2);
                } else
                {
                    //TBD
                }
                searchInput.setText("");
                errorText.setText("");
                searchLock = 0;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET
            }, 10);

            return;
        }
        else { }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
        }
    }

    public void searchButtonClick(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchLock == 0) {
                    searchString = searchInput.getText().toString();

                    //Checks that search length is valid(greater than 1 characters).
                    if (searchString.length() > 0) {
                        searchLock = 1;
                        errorText.setText("Searching for \"" + searchString + "\" in database...");
                        locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
                        timeout();
                    } else {
                        searchInput.setHint("Enter a valid search...");
                    }
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

    public int searchLocExist(String searchedLoc){

        //If method returns null, path not found in database.
        if (currentLoc != null) {
            Vertice startV = MapController.getInstance().getVerticeByLatLng(currentLoc);

            if (MapController.getInstance().GetPathFromFireBase(startV, searchedLoc) != null)
                return 1;
            else
                return 0;
        }
        return 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 2){
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
                    nextActivityIntent.putExtra("destString", searchString);
                    startActivity(nextActivityIntent);
                }
            }
        }
    }

    public void timeout() {

        timeoutTimer = new CountDownTimer(timeoutTime, 1000) {
            public void onTick(long millisUntilFinished) {
                //Nothing here!
            }

            public void onFinish() {
                locationManager.removeUpdates(locationListener);
                errorText.setText("Unable to find your current location. Make sure airplane mode is disabled and you are not in an enclosed space.");
                searchLock = 0;

                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        //Nothing here!
                    }

                    public void onFinish() {
                        errorText.setText("");
                    }
                }.start();
            }
        };

        timeoutTimer.start();
    }
}
