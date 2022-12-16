package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationListenerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.system_for_safe_road.databinding.ActivityUserRouteGpsactivityBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UserRoute_GPSActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    
    private GoogleMap mMap;
    private ActivityUserRouteGpsactivityBinding binding;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference2 =  firebaseDatabase.getReference().child("users"), databaseReference;
    int i=0, count;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.purple_700};
    private static final int[] COLORS1 = new int[]{R.color.mybeigeblue};
    android.location.LocationListener locationListener;
    private LocationManager locationManager;
    HashMap<LatLng, LatLng> hashMap = new HashMap<>();
    private final long MIN_TIME =1000;
    private final long MIN_DIST = 50;//5 for testing
    Marker sourceM, destinationM, tempM, tempM1;
    LatLng sourceL, destinationL, tempL, tempL1;
    int siz, col=0;
    String s, busid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserRouteGpsactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        polylines = new ArrayList<>();
        s = getIntent().getStringExtra("route");
        busid = getIntent().getStringExtra("busid");

        databaseReference = databaseReference2.child("routes").child(s);

        DatabaseReference databaseReference1= databaseReference.child("Flag");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

                        double latitude = (double) snapshot1.child("source").child("latitude").getValue();
                        double longitude = (double) snapshot1.child("source").child("longitude").getValue();
                        sourceL = new LatLng(latitude, longitude);
                        tempL1 = sourceL;
                        mMap.addMarker(new MarkerOptions().position(sourceL).title("source"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceL, 10));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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

                    col=0;
                    getRoute(latLng, latLng1);

                    i=i+1;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        siz = hashMap.size();

        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},  PackageManager.PERMISSION_GRANTED);



        /*final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //your method here
                            check();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);*/

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


        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                /*tempL1 = new LatLng(location.getLatitude(), location.getLongitude());
                if(tempL.equals("")){
                    tempL = tempL1;
                }
                mMap.addMarker(new MarkerOptions().position(tempL1).title("you"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tempL1));
                //getRoute(tempL, tempL1);
                databaseReference.child("gps").setValue(tempL1);
                //tempL = tempL1;*/
                tempL = new LatLng(location.getLatitude(), location.getLongitude());
                databaseReference2.child("bus_locations").child(busid).setValue(tempL);
                mMap.addMarker(new MarkerOptions().position(tempL).title(busid).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempL, 10));
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                String time = simpleDateFormat.format(date);
                databaseReference2.child("bus_locations").child(busid).child("time").setValue(time);
                Toast.makeText(UserRoute_GPSActivity.this, "n"+tempL1, Toast.LENGTH_SHORT).show();
                //Toast.makeText(UserRoute_GPSActivity.this, "a"+tempL1, Toast.LENGTH_SHORT).show();
                hashMap.put(tempL1, tempL);
                col=1;
                getRoute(tempL1, tempL);
                tempL1 = tempL;
                //check();
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, (android.location.LocationListener) locationListener);

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void check() {
        if(!hashMap.isEmpty()){
            for(LatLng i : hashMap.keySet()){
                LatLng latLng = i;
                LatLng latLng1 = hashMap.get(i);
                Toast.makeText(this, "abc"+latLng+latLng1, Toast.LENGTH_SHORT).show();
                getRoute(latLng, latLng1);
            }
        }
        else {
            return;
        }
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