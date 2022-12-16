package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tracking_Table extends AppCompatActivity {
    RecyclerView recyclerView;
    TrackAdapter adapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users"), databaseReference1;
    String busId, routeId, sourceString, destinationString;
    LatLng source, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_table);

        recyclerView = findViewById(R.id.track_table_id);

        busId = getIntent().getStringExtra("busID");
        databaseReference1 = databaseReference.child("trips").child(busId);


        setRecyclerView();


    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackAdapter(this, getList());
        recyclerView.setAdapter(adapter);
    }

    private List<TrackModel> getList(){
        List<TrackModel> trackModelList = new ArrayList<>();
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeId = snapshot.getValue().toString();
                Toast.makeText(Tracking_Table.this, ""+routeId, Toast.LENGTH_SHORT).show();
                databaseReference.child("routes").child(routeId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        source =new LatLng( (double) snapshot1.child("source").child("latitude").getValue(), (double) snapshot1.child("source").child("longitude").getValue());
                        destination =new LatLng( (double) snapshot1.child("destination").child("latitude").getValue(), (double) snapshot1.child("destination").child("longitude").getValue());
                        List<Address> addressList = null;
                        Geocoder geocoder = new Geocoder(Tracking_Table.this, Locale.getDefault());
                        try {
                            addressList = geocoder.getFromLocation(source.latitude, source.longitude, 1);
                            //addressList = geocoder.getFromLocation(destination.latitude, destination.longitude, 1);
                            sourceString = addressList.get(0).getAddressLine(0);
                            Toast.makeText(Tracking_Table.this, ""+sourceString, Toast.LENGTH_SHORT).show();
                            //destinationString = addressList.get(1).getAddressLine(0);
                            trackModelList.add(new TrackModel( sourceString, "a", "a", "a"));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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
        //Toast.makeText(this, ""+sourceString, Toast.LENGTH_SHORT).show();
        trackModelList.add(new TrackModel("destinationString", "a", "a", "a"));
        return trackModelList;
    }
}