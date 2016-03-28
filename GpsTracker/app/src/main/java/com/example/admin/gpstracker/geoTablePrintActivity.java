package com.example.admin.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class geoTablePrintActivity extends AppCompatActivity {

   private Button closeGeopointTable;
   private TextView geopointTableText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_table_print);

        closeGeopointTable = (Button) findViewById(R.id.geopoinTableButton);
        geopointTableText = (TextView) findViewById(R.id.geopointTable);

        Intent intent = getIntent();
        GeopointTable geopointTable = (GeopointTable) intent.getSerializableExtra("table");

        for(int i = 0; i < geopointTable.getGeopointCount(); i++)
            geopointTableText.append(i+1 + ". Lat: " + geopointTable.getGeopointByIndex(i).getLat() + " Long: " + geopointTable.getGeopointByIndex(i).getLng() + "\n");

        closeGeopointTableClick();
    }

    public void closeGeopointTableClick(){
        closeGeopointTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
