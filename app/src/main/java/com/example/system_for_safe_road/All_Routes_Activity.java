package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.system_for_safe_road.databinding.ActivityUserRouteGpsactivityBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class All_Routes_Activity extends AppCompatActivity  {

   DatabaseReference reference_route;
   AutoCompleteTextView autoCompleteTextView;
   ArrayList<String> stringList = new ArrayList<>();
   ArrayAdapter<String> arrayAdapter;
   Button confirm_button;
   String pass_to_next;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);

        reference_route= FirebaseDatabase.getInstance().getReference().child("users").child("routes");
        autoCompleteTextView=findViewById(R.id.auto_complete_txt_all_routes);
        confirm_button= findViewById(R.id.confirm_button_allroutes);



        preset();



    }

    public void preset()
    {
        reference_route.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String s = dataSnapshot.getKey();
                    stringList.add(s);
                    arrayAdapter = new ArrayAdapter<>(All_Routes_Activity.this, R.layout.route_list, stringList);
                    autoCompleteTextView.setAdapter(arrayAdapter);
                }

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        pass_to_next = adapterView.getItemAtPosition(i).toString();
                    }
                });

                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(All_Routes_Activity.this, Track_Edit_Route_Admin.class);
                        intent.putExtra("RouteId", pass_to_next);
                        startActivity(intent);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



//
//    @Override
//    public void onRoutingFailure(RouteException e) {

//    }
//
//    @Override
//    public void onRoutingStart() {
//
//
//    }
//






//
//    @Override
//    public void onRoutingCancelled() {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
}