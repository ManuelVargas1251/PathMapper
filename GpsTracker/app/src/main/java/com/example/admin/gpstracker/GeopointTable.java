package com.example.admin.gpstracker;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Admin on 3/26/2016.
 */
public class GeopointTable implements Serializable {

    private Vector<Geopoint> geopointVector;
    private int geopointCount;

    public GeopointTable(){
        //geopointVector = new Vector<>(50, 10);
        geopointVector = new Vector<>();
        geopointCount = 0;
    }

    public void addGeopoint(Geopoint newGeopoint){
        geopointVector.addElement(newGeopoint);
        geopointCount++;
    }

    public Geopoint getGeopointByIndex(int index){ return geopointVector.elementAt(index); }

    public int getGeopointCount(){ return geopointCount; }

    public void setEndsNextGeopoint(Geopoint geopoint) {
        geopointVector.elementAt(geopointCount-2).setNextGeopoint(geopoint);

       if(geopoint != null)
           setdistanceToNext(geopointVector.elementAt(geopointCount - 2), geopoint);
    }

    public void setdistanceToNext(Geopoint geopoint1, Geopoint geopoint2) {

        double lat1 = geopoint1.getLat();
        double lng1 = geopoint1.getLng();
        double lat2 = geopoint2.getLat();
        double lng2 = geopoint2.getLng();

        double theta = lng1 - lng2;

        double distance = Math.sin(lat1 * Math.PI / 180.0) * Math.sin(lat2 * Math.PI / 180.0) + Math.cos(lat1 * Math.PI / 180.0) * Math.cos(lat2 * Math.PI / 180.0) * Math.cos(theta* Math.PI / 180.0);
        distance = (Math.acos(distance) * 180.0 / Math.PI);

        distance = distance * 60 * 1.1515; //Miles
        distance *= 1609.344; //Miles to Meters

        geopoint1.setNextDistance(distance);

    }
}
