package com.example.admin.pathmapper;

//Standard and Content Imports
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

//Google Map Imports
import com.example.admin.pathmapper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;

public class createPathActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean firstLoop = true;

    private Button button, backButton;

    private TextView geopointTableTextView;

    private GeopointTable geopointTable = new GeopointTable();
    private Geopoint newGeopoint, prevGeopoint = null;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng newLoc, prevLoc;
    private Polyline line;

    private GoogleMap newMap;

    private boolean buttonStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_path);

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFrag.getMapAsync(this);

        button = (Button) findViewById(R.id.button);
        backButton = (Button) findViewById(R.id.backButton);
        geopointTableTextView = (TextView) findViewById(R.id.geopointTable);

        backButtonClick();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                newLoc = new LatLng(location.getLatitude(), location.getLongitude());

                newMap.moveCamera(CameraUpdateFactory.zoomTo(18));

                newGeopoint = new Geopoint(location.getLatitude(), location.getLongitude(), prevGeopoint, null);
                geopointTable.addGeopoint(newGeopoint);

                if(!firstLoop) {
                        // Add a thin red line from London to New York.
                        line = newMap.addPolyline(new PolylineOptions()
                                .add(prevLoc, newLoc)
                                .width(5)
                                .color(Color.RED));

                    geopointTable.setEndsNextGeopoint(newGeopoint);
                }

                prevGeopoint = newGeopoint;

                prevLoc = newLoc;
                firstLoop = false;

                newMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        }
        else {
            configureButton();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonStatus){
                    locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
                    button.setBackgroundColor(Color.RED);
                    button.setText("Stop Receive Locations");
                    buttonStatus = true;
                }
                else{
                    locationManager.removeUpdates(locationListener);
                    button.setBackgroundColor(Color.GREEN);
                    button.setText("Receive Locations");
                    buttonStatus = false;

                   geopointTableTextView.setText("");

                    for(int i = 0; i < geopointTable.getGeopointCount(); i++)
                            geopointTableTextView.append((i+1 + ". Lat: " + geopointTable.getGeopointByIndex(i).getLat() + " Long: " + geopointTable.getGeopointByIndex(i).getLng() + "\n\t-Dist to Next: " + geopointTable.getGeopointByIndex(i).getNextDistance() + "\n\n"));
                }
            }
        });
    }

    public void backButtonClick(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
              //  Intent intent = new Intent(getApplicationContext(), geoTablePrintActivity.class);

               // intent.putExtra("table", geopointTable);

               // startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        newMap = map;
        newMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        newMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.2075, -97.1526)));
    }
}
