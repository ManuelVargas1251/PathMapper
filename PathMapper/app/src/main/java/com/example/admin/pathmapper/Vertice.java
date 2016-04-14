package com.example.admin.pathmapper;

/**
 * Created by LOCHUYNH on 4/8/2016.
 */
public class Vertice {
    private String _name;

    private String _key;
    private double _lng;
    private double _lat;


    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public double getLng() {
        return _lng;
    }

    public void setLng(double _lng) {
        this._lng = _lng;
    }

    public double getLat() {
        return _lat;
    }

    public void setLat(double _lat) {
        this._lat = _lat;
    }

    public String getKey() {
        return _key;
    }

    public void setKey(String _key) {
        this._key = _key;
    }
}
