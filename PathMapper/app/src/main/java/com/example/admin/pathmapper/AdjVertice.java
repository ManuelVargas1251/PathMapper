package com.example.admin.pathmapper;

/**
 * Created by LOCHUYNH on 4/9/2016.
 */
public class AdjVertice {
    private String _srcVeticeId;
    private String _desVerticeId;
    private double _cost;

    public String getSourceVeticeId() {
        return _srcVeticeId;
    }

    public void setSourceVeticeId(String _srcVeticeId) {
        this._srcVeticeId = _srcVeticeId;
    }

    public String getDestinationVerticeId() {
        return _desVerticeId;
    }

    public void setDestinationVerticeId(String _desVerticeId) {
        this._desVerticeId = _desVerticeId;
    }

    public double getCost() {
        return _cost;
    }

    public void setCost(double _cost) {
        this._cost = _cost;
    }
}
