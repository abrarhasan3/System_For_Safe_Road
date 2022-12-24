package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.system_for_safe_road.databinding.ActivityBusTrackingMapBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bus_Tracking_Map_Activity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private ActivityBusTrackingMapBinding binding;
    String busIdString, routeIdString;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users"), databaseReference1, databaseReference2, databaseReference3;
    DatabaseReference trackingBusReference = firebaseDatabase.getReference().child("users").child("track");
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.purple_700};
    private static final int[] COLORS1 = new int[]{R.color.mybeigeblue};
    int i=0, count, col=0;
    HashMap<LatLng, LatLng> flagHashMap = new HashMap<>();
    Button refreshBtn;
    LatLng sourceLatLng, desLatLng;
    Marker tempM, routeM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBusTrackingMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        polylines = new ArrayList<>();
        busIdString = getIntent().getStringExtra("busID");
        refreshBtn = findViewById(R.id.refreshLocation);
        trackingBusReference = trackingBusReference.child(busIdString);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference1 = databaseReference.child("trips").child("routeID").child(busIdString);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                routeIdString = snapshot1.getValue().toString();
                trackingBusReference = trackingBusReference.child(routeIdString);

                databaseReference3 = databaseReference.child("routes").child(routeIdString);
                databaseReference3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        sourceLatLng = new LatLng((Double) snapshot2
                                .child("source").child("latitude").getValue(),
                                (Double) snapshot2.child("source").child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("source"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));

                        desLatLng = new LatLng((Double) snapshot2
                                .child("destination").child("latitude").getValue(),
                                (Double) snapshot2.child("destination").child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(desLatLng).title("destination"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desLatLng));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseReference2 = databaseReference.child("routes")
                        .child(routeIdString).child("Flag");
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        count = (int) snapshot.getChildrenCount();
                        while(i<count){
                            double latiude = (double) snapshot
                                    .child("Flag"+i).child("key").child("latitude").getValue();
                            double longitude = (double) snapshot
                                    .child("Flag"+i).child("key").child("longitude").getValue();
                            LatLng latLng = new LatLng(latiude, longitude);
                            if(i==0){
                                sourceLatLng=latLng;
                            }

                            mMap.addMarker(new MarkerOptions().position(latLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                            double latiude1 = (double) snapshot
                                    .child("Flag"+i).child("value").child("latitude").getValue();
                            double longitude1 = (double) snapshot
                                    .child("Flag"+i).child("value").child("longitude").getValue();
                            LatLng latLng1 = new LatLng(latiude1, longitude1);

                            mMap.addMarker(new MarkerOptions().position(latLng1));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));

                            //getRoute(latLng, latLng1);

                            col=0;
                            addValueToHashMap(latLng, latLng1);
                            i=i+1;

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
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Bus_Tracking_Map_Activity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                trackingBusReference.child("latlng_schedule").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LatLng tempL = sourceLatLng;
                        if(snapshot.exists()){
                            for(DataSnapshot i:snapshot.getChildren()){
                                double latitude = (double) i.child("latitude").getValue();
                                double longitude = (double) i.child("longitude").getValue();
                                LatLng tempL1 = new LatLng(latitude, longitude);
                                col=1;
                                getRoute(tempL, tempL1);
                                tempL1 = tempL;
                                routeM =  mMap.addMarker(new MarkerOptions().position(tempL1)
                                        .title(busIdString+"_"+i.getKey())
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempL1, 15));
                                //Toast.makeText(Bus_Tracking_Map_Activity.this, ""+i.getKey()+" "+latitude, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseReference.child("bus_locations").child(busIdString).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LatLng busLatLng = new LatLng((Double) snapshot.child("latitude").getValue(), (Double) snapshot.child("longitude").getValue());
                        if(tempM!=null){
                            tempM.remove();
                        }
                        tempM = mMap.addMarker(new MarkerOptions().position(busLatLng)
                                .title(busIdString+"_current location")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLatLng, 15));
                        col=1;
                        //getRoute(sourceLatLng, busLatLng);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void addValueToHashMap(LatLng latLng, LatLng latLng1) {
        flagHashMap.put(latLng, latLng1);
        getRoute(latLng, latLng1);
    }

    private void getRoute(LatLng latLng, LatLng latLng1) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(latLng, latLng1)
                .key("AIzaSyC2QPQUJKkWhPbdNnLCkrCOdxDmRCqim5k")
                .build();
        routing.execute();

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortest) {
        if(col==0){
            for (int i = 0; i <route.size(); i++) {

                //In case of more than 5 alternative routes
                int colorIndex = i % COLORS.length;

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(COLORS[colorIndex]));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(route.get(i).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);

            }
        }
        else {
            for (int i = 0; i <route.size(); i++) {

                //In case of more than 5 alternative routes
                int colorIndex = i % COLORS.length;

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(COLORS1[colorIndex]));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(route.get(i).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
}