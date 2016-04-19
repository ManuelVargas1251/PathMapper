package com.example.admin.pathmapper;

import java.util.List;

/**
 * Created by LOCHUYNH on 4/9/2016.
 */
public class Path {
    private String _srcVertexId;
    private String _desVertexId;
    private double _cost;
    private List<String> _pathDetail;
    private String _srcName;
    private String _desName;

    public String getSrcVertexId() {
        return _srcVertexId;
    }

    public void setSrcVertexId(String _srcVertexId) {
        this._srcVertexId = _srcVertexId;
    }

    public String getDesVertexId() {
        return _desVertexId;
    }

    public void setDesVertexId(String _desVertexId) {
        this._desVertexId = _desVertexId;
    }

    public List<String> getPathDetail() {
        return _pathDetail;
    }

    public void setPathDetail(List<String> _pathDetail) {
        this._pathDetail = _pathDetail;
    }

    public double getCost() {
        return _cost;
    }

    public void setCost(double _cost) {
        this._cost = _cost;
    }

    public String getSrcName() {
        return _srcName;
    }

    public void setSrcName(String _srcName) {
        this._srcName = _srcName;
    }

    public String getDesName() {
        return _desName;
    }

    public void setDesName(String _desName) {
        this._desName = _desName;
    }
}
