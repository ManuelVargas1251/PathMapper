package com.example.admin.pathmapper;

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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

//Firebase Imports
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createPathActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean firstLoop = true;
    private int nextActivity = 0;
    private int answer = 0;
    private String destString;

    private Button button, backButton;

    private TextView geopointTableTextView;

    private GeopointTable geopointTable = new GeopointTable();
    private Geopoint newGeopoint, prevGeopoint = null;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng newLoc, prevLoc;
    private Polyline line;

    private GoogleMap newMap;

    private Vertice _currentVertice = null;

    private boolean buttonStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
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

                    geopointTable.setdistanceToNext(prevGeopoint, newGeopoint);
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
                    locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
                    button.setBackgroundColor(Color.RED);
                    button.setText("Press At Destination");
                    buttonStatus = true;
                }
                else{
                    locationManager.removeUpdates(locationListener);
                    Intent intent = new Intent(getApplicationContext(), popCreatePath.class);
                    intent.putExtra("nextActivity", 0);
                    startActivityForResult(intent, 1);

                    //geopointTableTextView.setText("");

                    //for(int i = 0; i < geopointTable.getGeopointCount(); i++)
                    //        geopointTableTextView.append((i+1 + ". Lat: " + geopointTable.getGeopointByIndex(i).getLat() + " Long: " + geopointTable.getGeopointByIndex(i).getLng() + "\n\t-Dist to Next: " + geopointTable.getGeopointByIndex(i).getNextDistance() + "\n\n"));
                }
            }
        });
    }

    public void backButtonClick(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              nextActivity = 1;
              Intent intent = new Intent(getApplicationContext(), popCreatePath.class);
              intent.putExtra("nextActivity", 1);
              startActivityForResult(intent, 1);
            }
        });
    }

    public void uploadPath(){
        //lochuynh & Zachary Serna: create vertex, then add it to firebase.

        for(int i = 0; i < geopointTable.getGeopointCount(); i++){

            Vertice v = new Vertice();
            v.setLat(geopointTable.getGeopointByIndex(i).getLat());
            v.setLng(geopointTable.getGeopointByIndex(i).getLng());

            //need a vertex name, so we can find it by name. Uses UUID to create unique names for non-destinations.
            if((i-1) != geopointTable.getGeopointCount())
                v.setName(UUID.randomUUID().toString());
            else
                v.setName(destString);

            //save to firebase
            Firebase ref = new Firebase("https://torrid-fire-6521.firebaseio.com/PathMapper");

            //push a new info.
            Firebase verticesRef = ref.child("Vertices");
            Firebase vertex = verticesRef.push();

            // add vertice to firebase
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", v.getName());
            map.put("Longitude", String.valueOf(v.getLng()));
            map.put("Latitude", String.valueOf(v.getLat()));
            vertex.setValue(v);

            // Get the unique ID generated by push()
            String vertexId = vertex.getKey();
            v.setKey(vertexId);

            if (_currentVertice != null) {
                //need to update the adjacent list, since the the _currentVertice is the
                //previous vertex, and the vertex v should be adjacent with the previous vertex
                AdjVertice adj = new AdjVertice();
                adj.setSourceVeticeId(_currentVertice.getKey());
                adj.setDestinationVerticeId(vertexId);
                adj.setCost(10); //temporary hard code here, will calculate later

                //add adjacentVertice to firebase.
                Firebase refMapper = new Firebase("https://torrid-fire-6521.firebaseio.com/PathMapper");

                //push a new info.
                Firebase adjRef = refMapper.child("Adjacents");
                Firebase adjFileBase = adjRef.push();

                Map<String, String> adjHashMap = new HashMap<String, String>();
                adjHashMap.put("sourceId", adj.getSourceVeticeId());
                adjHashMap.put("destinationId", adj.getDestinationVerticeId());
                adjHashMap.put("cost", String.valueOf(adj.getCost()));

                adjFileBase.setValue(adj);
                //end
            } else
                _currentVertice = v;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        newMap = map;
        newMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        newMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(33.2075, -97.1526)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            answer = data.getIntExtra("answer", 0);
            if(resultCode == popNextActivity.RESULT_OK) {
                if(nextActivity == 0 && answer == 1){
                    destString = data.getStringExtra("destString");
                    uploadPath();
                    finish();
                }
                else
                    finish();

                if (nextActivity == 1 && answer == 1){
                    locationManager.removeUpdates(locationListener);
                    finish();
                }
            }
        }
    }
}
