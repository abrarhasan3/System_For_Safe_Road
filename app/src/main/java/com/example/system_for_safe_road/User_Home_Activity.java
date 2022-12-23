package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_Home_Activity extends AppCompatActivity {

    LinearLayout layout;
    String busid, s2, startTime, routeId;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout=findViewById(R.id.start_journey);
        busid = getIntent().getStringExtra("busid");
        routeId=getIntent().getStringExtra("route");
        startTime=getIntent().getStringExtra("startTime");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setBackgroundResource(R.color.mybeigeblue2);/*
                String ts = "Please wait";
                Toast toast = Toast.makeText(getApplicationContext(), ts, Toast.LENGTH_SHORT);
                View toastView = toast.getView();
                toastView.setBackgroundResource(R.color.mybeigeblue2);
                toast.show();*/
                Intent intent = new Intent(User_Home_Activity.this, UserRoute_GPSActivity.class);
                intent.putExtra("route", routeId);
                intent.putExtra("busid", busid);
                intent.putExtra("startTime", startTime);
                startActivity(intent);
            }
        });

    }
}