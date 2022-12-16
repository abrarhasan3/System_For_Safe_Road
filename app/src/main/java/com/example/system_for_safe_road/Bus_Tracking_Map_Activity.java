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
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.purple_700};
    private static final int[] COLORS1 = new int[]{R.color.mybeigeblue};
    int i=0, count, col=0;
    HashMap<LatLng, LatLng> flagHashMap = new HashMap<>();
    Button refreshBtn;
    LatLng sourceLatLng, desLatLng;
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

        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference1 = databaseReference.child("trips").child(busIdString);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                routeIdString = snapshot1.getValue().toString();

                databaseReference3 = databaseReference.child("routes").child(routeIdString);
                databaseReference3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        sourceLatLng = new LatLng((Double) snapshot2.child("source").child("latitude").getValue(), (Double) snapshot2.child("source").child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("source"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));

                        desLatLng = new LatLng((Double) snapshot2.child("destination").child("latitude").getValue(), (Double) snapshot2.child("destination").child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(desLatLng).title("destination"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desLatLng));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseReference2 = databaseReference.child("routes").child(routeIdString).child("Flag");
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        count = (int) snapshot.getChildrenCount();
                        while(i<count){
                            double latiude = (double) snapshot.child("Flag"+i).child("key").child("latitude").getValue();
                            double longitude = (double) snapshot.child("Flag"+i).child("key").child("longitude").getValue();
                            LatLng latLng = new LatLng(latiude, longitude);

                            mMap.addMarker(new MarkerOptions().position(latLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                            double latiude1 = (double) snapshot.child("Flag"+i).child("value").child("latitude").getValue();
                            double longitude1 = (double) snapshot.child("Flag"+i).child("value").child("longitude").getValue();
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
                databaseReference.child("bus_locations").child(busIdString).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LatLng busLatLng = new LatLng((Double) snapshot.child("latitude").getValue(), (Double) snapshot.child("longitude").getValue());
                        mMap.addMarker(new MarkerOptions().position(busLatLng).title(busIdString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLatLng, 15));
                        for(LatLng latLng : flagHashMap.keySet()){
                            Location start = new Location("A");
                            start.setLatitude(busLatLng.latitude);
                            start.setLongitude(busLatLng.longitude);

                            Location end = new Location("B");
                            end.setLatitude(latLng.latitude);
                            end.setLongitude(latLng.longitude);

                            double dist = start.distanceTo(end);
                            if(dist<50){
                                mMap.addMarker(new MarkerOptions().position(latLng).title("reached").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            }
                            //Toast.makeText(Bus_Tracking_Map_Activity.this, ""+flagHashMap.get(latLng), Toast.LENGTH_SHORT).show();
                        }
                        col=1;
                        getRoute(sourceLatLng, busLatLng);
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