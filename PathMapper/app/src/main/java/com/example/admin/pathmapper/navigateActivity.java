package com.example.admin.pathmapper;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class navigateActivity extends AppCompatActivity implements OnMapReadyCallback {

    int distanceFt, timeToDestination;
    private String destString;

    private GeopointTable geopointTable = new GeopointTable();
    private Geopoint newGeopoint, prevGeopoint = null;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng newLoc;
    private Polyline line;

    private GoogleMap newMap;
    private Marker marker, destMarker;

    private Button backButton;
    private TextView infoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        Intent intent = getIntent();
        destString = intent.getStringExtra("destString");

        infoPane = (TextView) findViewById(R.id.infoPane);
        backButton = (Button) findViewById(R.id.backButton);
        backButton();

        //getPath();
        //drawPath();

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFrag.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                newLoc = new LatLng(location.getLatitude(), location.getLongitude());

                //Creates maker of user position.
                if(marker != null)
                    marker.remove();

                marker = newMap.addMarker(new MarkerOptions()
                                                .position(newLoc)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                newMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                newMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));

                if(!_isHavingPath)
                    getPath();

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
        else {
            getLocation();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                   getLocation();

                return;
        }
    }

    public void getLocation(){
        locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
    }

    public void backButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
                finish();
            }
        });
    }

    boolean _isHavingPath = false;

    public void getPath() {
        //Enter code to get path
        //lochuynh: get path to navigate here.
        //get current location, then build the source vertex
        if (newLoc == null)
            getLocation();

        //build source vertex.
        Vertice srcVertex = MapController.getInstance().getVerticeByLatLng(newLoc);
        Vertice desVertex = MapController.getInstance().getVerticeByName(destString);

        Path p = MapController.getInstance().GetPathFromFireBase(srcVertex, desVertex);
        if (p != null) {
            _isHavingPath = true;

            //create geopoint table, then draw path
            Geopoint srcPoint = new Geopoint(srcVertex.getLat(), srcVertex.getLng());
            geopointTable.addGeopoint(srcPoint);

            List<String> lstDetail = p.getPathDetail();
            for (int i = 0; i < lstDetail.size(); i++) {
                String key = lstDetail.get(i);
                Vertice midVertice = MapController.getInstance().getVerticeByKey(key);
                Geopoint midPoint = new Geopoint(midVertice.getLat(), midVertice.getLng());
                geopointTable.addGeopoint(midPoint);
            }

            Geopoint desPoint = new Geopoint(desVertex.getLat(), desVertex.getLng());
            geopointTable.addGeopoint(desPoint);

            //final, draw path
            drawPath();
        }
    }

    public void drawPath(){

        distanceFt = 0;

        for(int i = 0; i < geopointTable.getGeopointCount(); i++) {
            //Builds Polyline to navigate.
            line = newMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(geopointTable.getGeopointByIndex(i-1).getLat(),
                                    geopointTable.getGeopointByIndex(i-1).getLng()),
                         new LatLng(geopointTable.getGeopointByIndex(i).getLat(),
                                    geopointTable.getGeopointByIndex(i).getLng()))
                    .width(5)
                    .color(Color.GRAY));

            if((i-1) == geopointTable.getGeopointCount()) {
                destMarker = newMap.addMarker(new MarkerOptions()
                        .position(new LatLng(geopointTable.getGeopointByIndex(i).getLat(),
                                             geopointTable.getGeopointByIndex(i).getLng()))
                        .title(destString));
            }

            if(i != 0)
                distanceFt += (geopointTable.getGeopointByIndex(i-1).getNextDistance());
        }

        //Calculates Time to Walk and prints info.
        timeToDestination = (distanceFt/5)/60;
        infoPane.setText(" Time to Destination: " + timeToDestination + " min.\n Distance to: " + distanceFt + " ft.");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        newMap = map;
    }

    @Override
    public void onBackPressed() {
    }
}
