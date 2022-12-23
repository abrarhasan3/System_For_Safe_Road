package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bus_id_for_tracking extends AppCompatActivity {
    ArrayList<String> stringList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
    AutoCompleteTextView autoCompleteTextView;
    Button showBtn;
    String busIdString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_id_for_tracking);

        showBtn = findViewById(R.id.track_bus);
        Toast.makeText(this, "wait for toast to end", Toast.LENGTH_SHORT).show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.child("trips").child("routeID").getChildren()){
                    String s = dataSnapshot.getKey();
                    stringList.add(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        autoCompleteTextView = findViewById(R.id.bus_id_on_route);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.route_list, stringList);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                busIdString = parent.getItemAtPosition(position).toString();
            }
        });
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bus_id_for_tracking.this, Bus_Tracking_Map_Activity.class);
                intent.putExtra("busID", busIdString);
                startActivity(intent);
            }
        });
    }
}