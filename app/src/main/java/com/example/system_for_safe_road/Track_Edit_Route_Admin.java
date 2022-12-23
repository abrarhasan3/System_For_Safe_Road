package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Track_Edit_Route_Admin extends AppCompatActivity {

    String RouteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit_route_admin);

        RouteId = getIntent().getStringExtra("RouteId");



    }
}