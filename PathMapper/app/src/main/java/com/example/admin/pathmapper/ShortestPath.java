package com.example.admin.pathmapper;

import android.app.Activity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lochuynh on 4/17/16.
 */
public class ShortestPath {
    double[][] _cost_matrix;
    List<Vertice> _lstVertice = new ArrayList<Vertice>();
    List<AdjVertice> _lstAdjVertice = new ArrayList<AdjVertice>();

    public ShortestPath()
    {

    }

    public void BuildShortestPath() {
        //build shortest path by using Dijkstra
        populateAllVerticeFromFireBase();
        populateAllAdjacentFromFireBase();
    }

    //get all vertices, adjVertices from firebase, then build the cost matrix
    private void buildCostMatrix()
    {
        int numOfVertice = _lstVertice.size();
        _cost_matrix = new double[numOfVertice][numOfVertice];
        for (int i = 0; i < numOfVertice; i ++)
        {
            String srcVertexKey = _lstVertice.get(i).getKey();
            for (int j = 0; j < numOfVertice; j ++)
            {
                String desVertexKey = _lstVertice.get(j).getKey();
                if (i != j)
                    _cost_matrix[i][j] = getCostFormAdjacentList(srcVertexKey, desVertexKey);
                else
                    _cost_matrix[i][j] = 0;
            }
        }
    }

    //get cost between 2 vertices.
    double getCostFormAdjacentList(String srcVertexKey, String desVertexKey)
    {
        double cost = Double.MAX_VALUE;
        for (int i = 0; i < _lstAdjVertice.size(); i ++)
        {
            AdjVertice adj = _lstAdjVertice.get(i);
            if (adj.getSourceVeticeId().equals(srcVertexKey)
                    && adj.getDestinationVerticeId().equals(desVertexKey))
                return adj.getCost();
        }
        return cost;
    }

    private void runAllDijkstra()
    {
        if (_lstAdjVertice.size() == 0)
            return;

        //before start running the algorithm, we need to delete all existed paths in the firebase first.
        deleteAllExisedPath();

        //run Dijkstra on all vertices
        int numOfVertice = _lstVertice.size();
        for (int i = 0; i < numOfVertice; i ++)
            runDijkstra(i); //run Dijkstra on all vertices.
    }

    private void deleteAllExisedPath() {
        //delete path from firebase
        Firebase ref = new Firebase("https://torrid-fire-6521.firebaseio.com/PathMapper/Paths");
        ref.removeValue();
    }

    //run Dijkstra on each vertex to find the shortest path
    private void runDijkstra(int srcIdx)
    {
        int numOfVertice = _lstVertice.size();
        int lstVisited[] = new int[numOfVertice];
        double dist[] = new double[numOfVertice];
        int preDecessor[] = new int[numOfVertice]; //store predecessor of a vertex, we need this to build a path

        for (int i = 0; i < numOfVertice; i ++) {
            //Initialize
            lstVisited[i] = 0; //not visited any vertex so far
            dist[i] = _cost_matrix[srcIdx][i];
            preDecessor[i] = srcIdx; //initial all predecessor is the source vertex
        }

        //put source vertex in list Visited
        lstVisited[srcIdx] = 1;
        dist[srcIdx] = 0;

        for (int i = 0; i < numOfVertice; i ++) {
            //Choose u from among those vertices not in S such that
            //dist[u] is minimum;
            int u = getMinimumDistanceNotVisited(lstVisited, dist, numOfVertice);
            if (u != -1){
                //put u in visited list
                lstVisited[u] = 1;
                //for (each w adjacent to u with S[w] = false) do
                for (int w = 0; w < numOfVertice; w ++){
                    if (lstVisited[w] == 0) {
                        //if (dist[w] > dist[u] + cost[u,w]) then
                        //dist[w] := dist[u] + cost[u,w];
                        if (dist[w] > dist[u] + _cost_matrix[u][w]) {
                            dist[w] = dist[u] + _cost_matrix[u][w];
                            //store w as a predecessor of u
                            preDecessor[u] = w;
                        }
                    }
                }
            }

        }

        //done, dijkstra, need to update cost matrix, then create path and update to firebase.
        _cost_matrix[srcIdx] = dist;

        saveShortestPath(srcIdx, preDecessor, numOfVertice);
    }

    //add shortest path to FireBase
    private void saveShortestPath(int srcIdx, int[] preDecessor, int numOfVertice) {
        //save to firebase
        Firebase ref = new Firebase("https://popping-torch-1288.firebaseio.com/PathMapper");

        //push a new info.
        Firebase pathRef = ref.child("Paths");

        for (int i = 0; i < numOfVertice; i ++) {
            if (i != srcIdx
                    && _cost_matrix[srcIdx][i] < Double.MAX_VALUE) {

                //only add to firebase a real path
                Firebase pathFireBase = pathRef.push();

                //create path object
                Path p = new Path();

                p.setSrcVertexId(_lstVertice.get(srcIdx).getKey());
                p.setSrcName(_lstVertice.get(srcIdx).getName());
                p.setDesVertexId(_lstVertice.get(i).getKey());
                p.setDesName(_lstVertice.get(i).getName());
                p.setCost(_cost_matrix[srcIdx][i]);
                List<String> lstPathDetail = new ArrayList<String>();

                int pre = preDecessor[i];
                do {
                    lstPathDetail.add(_lstVertice.get(pre).getKey());
                    pre = preDecessor[pre];
                } while (pre != srcIdx);

                //reserve order to print path from source to dest
                Collections.reverse(lstPathDetail);
                p.setPathDetail(lstPathDetail);

                // add path to firebase
                pathFireBase.setValue(p);
            }
        }
    }

    private int getMinimumDistanceNotVisited(int lstVisited[], double dist[], int numOfVertice) {

        //Choose u from among those vertices not in S such that
        //dist[u] is minimum;
        int idx = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < numOfVertice; i++) {
            if (lstVisited[i] == 0)
            {
                if (minDistance > dist[i]) {
                    minDistance = dist[i];
                    idx = i;
                }
            }
        }
        return idx;
    }

    private void populateAllAdjacentFromFireBase()
    {
        // Use Firebase to populate the list.
        _lstAdjVertice.clear(); //clear all data at first

        Firebase ref = new Firebase("https://popping-torch-1288.firebaseio.com/PathMapper/Adjacents");
        Query queryRef = ref.orderByChild("cost");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                buildCostMatrix();
                runAllDijkstra();
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
                AdjVertice adj = snapshot.getValue(AdjVertice.class);

                _lstAdjVertice.add(adj);
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

    private void populateAllVerticeFromFireBase()
    {
        // Use Firebase to populate the list.
        _lstVertice.clear(); //clear all data at first

        Firebase ref = new Firebase("https://popping-torch-1288.firebaseio.com/PathMapper/Vertices");
        Query queryRef = ref.orderByChild("name");

        //need to ensure that the dijkstra algorithm has to run, since this is a callback function
        //we do not know when it will be done.
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                buildCostMatrix();
                runAllDijkstra();
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

}
