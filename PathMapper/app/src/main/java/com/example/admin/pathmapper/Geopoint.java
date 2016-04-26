package com.example.admin.pathmapper;

import java.io.Serializable;

/**
 * Created by Admin on 3/25/2016.
 */
public class Geopoint implements Serializable{

    private double lat;
    private double lng;
    private double distanceToNext;

    public Geopoint(double newLat, double newLng){
        lat = newLat;
        lng = newLng;
        distanceToNext = 0;
    }

    public double getLat(){ return lat; }

    public double getLng(){ return lng; }

    public double getNextDistance() { return distanceToNext; }

    public void setNextDistance(double nextDistance) { distanceToNext = nextDistance; }
}
