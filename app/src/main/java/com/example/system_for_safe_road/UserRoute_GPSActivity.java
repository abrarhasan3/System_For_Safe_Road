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
    int i=0, count, j=0;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.purple_700};
    private static final int[] COLORS1 = new int[]{R.color.mybeigeblue};
    android.location.LocationListener locationListener;
    private LocationManager locationManager;
    HashMap<LatLng, LatLng> hashMap = new HashMap<>();
    HashMap<LatLng, String> startTimeHashMap = new HashMap<>();
    HashMap<Integer, LatLng> flagHashMap = new HashMap<>();
    private final long MIN_TIME =100;
    private final long MIN_DIST = 5;//5 for testing
    Marker sourceM, destinationM, tempM, tempM1;
    LatLng sourceL, destinationL, tempL, tempL1;
    int siz, col=0;
    String s, busid, startTime, subTime;
    int hour , minute;


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
        startTime = getIntent().getStringExtra("startTime");
        siz = hashMap.size();
        int index = startTime.indexOf(":"), index2, index3;
        if(startTime.contains("a")){
            index2 = startTime.indexOf("a");
            minute = Integer.parseInt(startTime.substring(index+1, index2-1));
            subTime = " am";
        }
        else {
            index3 = startTime.indexOf("p");
            minute = Integer.parseInt(startTime.substring(index+1, index3-1));
            subTime = " pm";
        }
        hour = Integer.parseInt(startTime.substring(0, index));

        //Toast.makeText(this, ""+hour+" "+minute, Toast.LENGTH_SHORT).show();

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                        startTimeHashMap.put(sourceL, "0");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                count = (int) snapshot.getChildrenCount();
                while(j<count){
                    double latiude1 = (double) snapshot.child("Flag"+j).child("value").child("latitude").getValue();
                    double longitude1 = (double) snapshot.child("Flag"+j).child("value").child("longitude").getValue();
                    String flagtime = (String) snapshot.child("Flag"+j).child("time").getValue();
                    Integer flagtimeInt = Integer.parseInt(flagtime);
                    Integer flagMin = (flagtimeInt+minute)%60;
                    Integer flagHour = hour + ((flagtimeInt+minute)/60);
                    if(flagHour>12){
                        flagHour = flagHour%12;
                        if(subTime.trim()=="am"){
                            subTime=" "+"pm";
                        }
                        else {
                            subTime=" "+"am";
                        }
                    }
                    //Toast.makeText(UserRoute_GPSActivity.this, flagHour+":"+flagMin, Toast.LENGTH_SHORT).show();
                    String h = Integer.toString(flagHour), m = Integer.toString(flagMin);
                    String time = h+":"+m+subTime;
                    LatLng latLng1 = new LatLng(latiude1, longitude1);
                    startTimeHashMap.put(latLng1, time);
                    hour = flagHour;
                    minute = flagMin;
                    j=j+1;
                }
                while(i<count){
                    double latiude = (double) snapshot.child("Flag"+i).child("key").child("latitude").getValue();
                    double longitude = (double) snapshot.child("Flag"+i).child("key").child("longitude").getValue();
                    LatLng latLng = new LatLng(latiude, longitude);
                    if(i==0){
                        mMap.addMarker(new MarkerOptions().position(latLng).title("source"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(latLng).title(startTimeHashMap.get(latLng)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                    Toast.makeText(UserRoute_GPSActivity.this, ""+startTimeHashMap.get(latLng), Toast.LENGTH_SHORT).show();
                    double latiude1 = (double) snapshot.child("Flag"+i).child("value").child("latitude").getValue();
                    double longitude1 = (double) snapshot.child("Flag"+i).child("value").child("longitude").getValue();
                    String time = (String) snapshot.child("Flag"+i).child("time").getValue();
                    LatLng latLng1 = new LatLng(latiude1, longitude1);
                    mMap.addMarker(new MarkerOptions().position(latLng1).title(startTimeHashMap.get(latLng1)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));

                    //all the markers except source
                    flagHashMap.put(i, latLng1);

                    col=0;
                    getRoute(latLng, latLng1);

                    i=i+1;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                if(tempM!=null)
                {
                    tempM.remove();
                }
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempL, 10));
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                String time = simpleDateFormat.format(date);
                tempM = mMap.addMarker(new MarkerOptions().position(tempL).title(busid+"-"+time).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                databaseReference2.child("bus_locations").child(busid).child("time").setValue(time);
                Toast.makeText(UserRoute_GPSActivity.this, "n"+tempL1, Toast.LENGTH_SHORT).show();
                hashMap.put(tempL1, tempL);
                col=1;
                getRoute(tempL1, tempL);
                tempL1 = tempL;

                for(int i:flagHashMap.keySet())
                {

                    //Toast.makeText(UserRoute_GPSActivity.this, i+" "+flagHashMap.get(i), Toast.LENGTH_SHORT).show();
                }

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