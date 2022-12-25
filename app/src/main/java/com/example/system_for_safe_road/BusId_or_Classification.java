package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class BusId_or_Classification extends AppCompatActivity {
    LinearLayout classify;
    LinearLayout busid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_id_or_classification);
        classify=findViewById(R.id.classify);
        busid= findViewById(R.id.busidfornext);

        preset();
    }
    public void preset()
    {
        classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusId_or_Classification.this,ClassificationActicity.class);
                startActivity(intent);

            }
        });
        busid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusId_or_Classification.this,bus_id_page.class);
                startActivity(intent);
                finish();
            }
        });
    }
}