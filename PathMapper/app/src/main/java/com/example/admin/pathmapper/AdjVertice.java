package com.example.admin.pathmapper;

/**
 * Created by LOCHUYNH on 4/9/2016.
 */
public class AdjVertice {
    private String _srcVeticeId;
    private String _desVerticeId;
    private int _cost;

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

    public int getCost() {
        return _cost;
    }

    public void setCost(int _cost) {
        this._cost = _cost;
    }
}
