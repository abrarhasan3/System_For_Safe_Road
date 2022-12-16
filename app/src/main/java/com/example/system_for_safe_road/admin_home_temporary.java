package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class admin_home_temporary extends AppCompatActivity {
    Button trip, route, track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_temporary);

        trip = findViewById(R.id.addTrip);
        route = findViewById(R.id.setRoute);
        track = findViewById(R.id.trackBus);

        trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_home_temporary.this, Admin_Add_Trip.class);
                startActivity(intent);
            }
        });
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_home_temporary.this, AdminRouteSelectionActivity.class);
                startActivity(intent);
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_home_temporary.this, bus_id_for_tracking.class);
                startActivity(intent);
            }
        });
    }
}