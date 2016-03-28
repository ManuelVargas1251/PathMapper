package com.example.admin.gpstracker;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Admin on 3/26/2016.
 */
public class GeopointTable implements Serializable {

    private Vector<Geopoint> geoPointVector;
    private int geopointCount;

    public GeopointTable(){
        geoPointVector = new Vector<>(50, 10);
        geopointCount = 0;
    }

    public void addGeopoint(Geopoint newGeopoint){
        geoPointVector.addElement(newGeopoint);
        geopointCount++;
    }

    public Geopoint getGeopointByIndex(int index){ return geoPointVector.elementAt(index); }

    public int getGeopointCount(){ return geopointCount; }

    public void setEndsNextGeopoint(Geopoint geopoint) { geoPointVector.elementAt(geopointCount-2).setNextGeopoint(geopoint); }
}
