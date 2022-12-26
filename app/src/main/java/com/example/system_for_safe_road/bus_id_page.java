package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bus_id_page extends AppCompatActivity {
    EditText bus;
    Button busBtn;
    DrawerLayout drawerLayout;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
    String s2, s3;

    ArrayList<String> stringList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView autoCompleteTextView;
    String busIdString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_id_page);
        getSupportActionBar().hide();

        drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.user_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bus_id_page.this, BusId_or_Classification.class);
                startActivity(intent);
            }
        });

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

        busBtn = findViewById(R.id.confirm_button1);
        busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String busId = bus.getText().toString();
                Intent intent = new Intent(bus_id_page.this, User_Home_Activity.class);
                /*String ts = "Please wait";
                Toast toast = Toast.makeText(getApplicationContext(), ts, Toast.LENGTH_SHORT);
                View toastView = toast.getView();
                toastView.setBackgroundResource(R.color.mybeigeblue2);
                toast.show();*/
                DatabaseReference databaseReference1 = databaseReference.child("trips").child("routeID").child(busIdString);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        s2 = snapshot.getValue().toString();
                        DatabaseReference databaseReference2 = databaseReference.child("trips").child("startTime").child(busIdString);
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                s3 = snapshot.getValue().toString();
                                intent.putExtra("startTime", s3);
                                intent.putExtra("route", s2);
                                intent.putExtra("busid", busIdString);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                /*Intent intent = new Intent(bus_id_page.this, User_Home_Activity.class);
                intent.putExtra("busid", busId);
                startActivity(intent);*/

            }
        });
    }
}