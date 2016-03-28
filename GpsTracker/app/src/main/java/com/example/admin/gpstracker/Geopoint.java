package com.example.admin.gpstracker;

import java.io.Serializable;

/**
 * Created by Admin on 3/25/2016.
 */
public class Geopoint implements Serializable{

    private double lat;
    private double lng;
    private Geopoint prevGeopoint;
    private Geopoint nextGeopoint;

    public Geopoint(double newLat, double newLng, Geopoint newPrevGeopoint, Geopoint newNextGeopoint){
        lat = newLat;
        lng = newLng;
        prevGeopoint = newPrevGeopoint;
        nextGeopoint = newNextGeopoint;
    }

    public double getLat(){ return lat; }

    public double getLng(){ return lng; }

    public Geopoint getPrevGeopoint(){ return prevGeopoint; }

    public Geopoint getNextGeopoint(){ return nextGeopoint; }

    public void setNextGeopoint(Geopoint geopoint) {nextGeopoint = geopoint; }
}
