package com.example.admin.pathmapper;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lochuynh on 4/18/16.
 */
public final class MapController {

    private static volatile MapController instance;

    private MapController() {
        // TODO: Initialize
        // ...
    }

    /**
     * Get the only instance of this class.
     *
     * @return the single instance.
     */
    public static MapController getInstance() {
        if (instance == null) {
            synchronized (MapController.class) {
                if (instance == null) {
                    instance = new MapController();
                }
            }
        }
        return instance;
    }

    private List<Path> _lstPath = new ArrayList<Path>();
    private List<Vertice> _lstVertice = new ArrayList<Vertice>();
    private boolean _isAllPathLoaded = false;
    private boolean _isAllVerticeLoaded = false;
    private  android.content.Context  _context;

    public  void LoadData(android.content.Context ct) {
        _context = ct;
        populateAllVerticeFromFireBase();
        populateAllPathFromFireBase();
    }

    private  void populateAllVerticeFromFireBase(){
        // Use Firebase to populate the list.
        _lstVertice.clear(); //clear all data at first

        Firebase.setAndroidContext(_context);
        Firebase ref = new Firebase("https://popping-torch-1288.firebaseio.com/PathMapper/Vertices");

        Query queryRef = ref.orderByChild("name");

        //need to ensure that the dijkstra algorithm has to run, since this is a callback function
        //we do not know when it will be done.
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                _isAllVerticeLoaded = true;
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                //System.out.println(snapshot.getKey());
                //add Match object data to list.
                //get all match have not had a result yet
                Vertice v = snapshot.getValue(Vertice.class);

                //get the key for this vertice.
                v.setKey(snapshot.getKey());

                _lstVertice.add(v);
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private  void populateAllPathFromFireBase() {
        //reset all path first
        _lstPath.clear();

        //we need to get all paths from firebase, then save it to memmory first
        Firebase.setAndroidContext(_context);
        Firebase ref = new Firebase("https://popping-torch-1288.firebaseio.com/PathMapper/Paths");

        Query queryRef = ref.orderByChild("cost");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // flag to let caller know that all paths are available now
                _isAllPathLoaded = true;

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                //System.out.println(snapshot.getKey());
                //add Path object data to list.
                //get all path have not had a result yet
                Path p  = snapshot.getValue(Path.class);
                _lstPath.add(p);
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public  Path GetPathFromFireBase(Vertice startV, String desName)
    {
        Vertice desV = getVerticeByName(desName);
        if (desV != null)
            return GetPathFromFireBase(startV, desV);
        else
            return null;
    }

    public  Vertice getVerticeByName(String name) {
        for (int i = 0; i < _lstVertice.size(); i++) {
            Vertice v = _lstVertice.get(i);
            if (v.getName().toLowerCase().equals(name.toLowerCase()))
                return  v;
        }
        return null;
    }

    public  Vertice getVerticeByKey(String key) {
        for (int i = 0; i < _lstVertice.size(); i++) {
            Vertice v = _lstVertice.get(i);
            if (v.getKey().equals(key))
                return  v;
        }
        return null;
    }

    public   Path GetPathFromFireBase(Vertice startVertex, Vertice endVertice) {
        //return a Path from start to end from FireBase if any.
        Path actualPath = null;
        for (int i = 0; i < _lstPath.size(); i ++) {
            Path p = _lstPath.get(i);
            if (p.getSrcName().toLowerCase().equals(startVertex.getName().toLowerCase())
                    && p.getDesName().toLowerCase().equals(endVertice.getName().toLowerCase()))
            {
                actualPath = p;
                break;
            }
        }
        return actualPath;
    }

    public  boolean isAllPathLoaded() {
        return _isAllPathLoaded;
    }

    public  boolean IsAllDataLoaded()
    {
        return (_isAllPathLoaded && _isAllVerticeLoaded);
    }

    //return the approxiamate vertex from LatLng
    public  Vertice getVerticeByLatLng(LatLng loc) {
        if (!_isAllVerticeLoaded)
            return null;

        double currentLng = loc.longitude;
        double currentLat = loc.latitude;
        double exactDistance = .001;
        double approximateDistance = 10;

        for (int i = 0; i < _lstVertice.size(); i++) {
            Vertice v = _lstVertice.get(i);
            double distanceLat = Math.abs(v.getLat() - currentLat);
            double distanceLng = Math.abs(v.getLng() - currentLng);

            if (distanceLat <= exactDistance
                    && distanceLng <= exactDistance) {
                return v;
            }
        }

        //Find approximate distance
        for (int i = 0; i < _lstVertice.size(); i++){
            Vertice v = _lstVertice.get(i);
            double distanceLat = Math.abs(v.getLat() - currentLat);
            double distanceLng = Math.abs(v.getLng() - currentLng);

            if (distanceLat <= approximateDistance
                    && distanceLng <= approximateDistance) {
                return v;
            }
        }
        return null;
    }
}
