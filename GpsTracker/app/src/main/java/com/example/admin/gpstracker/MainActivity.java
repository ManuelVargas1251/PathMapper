package com.example.admin.gpstracker;

//Standard and Content Imports
import android.Manifest;
import android.content.pm.PackageManager;
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
import android.content.Intent;

//Google Map Imports
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Button button;
    private TextView textView;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng newLoc;

    private GoogleMap newMap;
    private CameraUpdateFactory camera;

    private boolean buttonStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFrag.getMapAsync(this);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                textView.setText("Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());
                newLoc = new LatLng(location.getLatitude(), location.getLongitude());

                newMap.moveCamera(CameraUpdateFactory.zoomTo(18));

                newMap.addMarker(new MarkerOptions()
                        .position(newLoc)
                        .title(""));

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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        } else {
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
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                    button.setBackgroundColor(Color.RED);
                    button.setText("Stop Receive Locations");
                    buttonStatus = true;
                }
                else{
                    locationManager.removeUpdates(locationListener);
                    button.setBackgroundColor(Color.GREEN);
                    button.setText("Receive Locations");
                    textView.setText("Lat: , Lat: ");
                    buttonStatus = false;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        newMap = map;
        newMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        newMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.2075, -97.1526)));
/*
        newMap.addMarker(new MarkerOptions()
                .position(new LatLng(33.2075, -97.1526))
                .title("UNT"));
*/
    }
}
