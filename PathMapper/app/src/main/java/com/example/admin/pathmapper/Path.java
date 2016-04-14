package com.example.admin.pathmapper;

import java.util.List;

/**
 * Created by LOCHUYNH on 4/9/2016.
 */
public class Path {
    private String _srcVertexId;
    private String _desVertexId;
    private List<Vertice> _pathDetail;

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

    public List<Vertice> getPathDetail() {
        return _pathDetail;
    }

    public void setPathDetail(List<Vertice> _pathDetail) {
        this._pathDetail = _pathDetail;
    }
}
