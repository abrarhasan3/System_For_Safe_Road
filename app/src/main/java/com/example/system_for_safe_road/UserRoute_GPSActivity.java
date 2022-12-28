package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationListenerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.ParseException;
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
    DatabaseReference trackingBusReference = firebaseDatabase
            .getReference().child("users").child("track");
    DatabaseReference databaseReference2 =  firebaseDatabase
            .getReference().child("users"), databaseReference;
    int i=0, count, j=0, checkLag=0;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.purple_700};
    private static final int[] COLORS1 = new int[]{R.color.mybeigeblue};
    android.location.LocationListener locationListener;
    private LocationManager locationManager;
    HashMap<LatLng, LatLng> hashMap = new HashMap<>();
    HashMap<LatLng, String> startTimeHashMap = new HashMap<>();
    HashMap<Integer, LatLng> flagHashMap = new HashMap<>();
    private final long MIN_TIME =100;
    private final long MIN_DIST = 50;//5 for testing
    Marker sourceM, destinationM, tempM, tempM1;
    LatLng sourceL, destinationL, tempL, tempL1, tempL_schedule;
    int siz, col=0;
    String s, busid, startTime, subTime;
    int hour , minute;
    public static int myfollowingFlag = 0, flagInt=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserRouteGpsactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_Track_Admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        polylines = new ArrayList<>();
        s = getIntent().getStringExtra("route");
        busid = getIntent().getStringExtra("busid");
        startTime = getIntent().getStringExtra("startTime");
        siz = hashMap.size();
        trackingBusReference = trackingBusReference.child(busid).child(s);
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
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},  PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_COARSE_LOCATION},  PackageManager.PERMISSION_GRANTED);

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
                    //Toast.makeText(UserRoute_GPSActivity.this, i+" "+count, Toast.LENGTH_SHORT).show();
                    double latiude = (double) snapshot.child("Flag"+i).child("key").child("latitude").getValue();
                    double longitude = (double) snapshot.child("Flag"+i).child("key").child("longitude").getValue();
                    LatLng latLng = new LatLng(latiude, longitude);
                    if(i==0){
                        sourceL = latLng;
                        tempL_schedule = sourceL;
                        mMap.addMarker(new MarkerOptions().position(latLng).title("source"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(latLng).title(startTimeHashMap.get(latLng)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                    //Toast.makeText(UserRoute_GPSActivity.this, ""+startTimeHashMap.get(latLng), Toast.LENGTH_SHORT).show();
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
                    if(i==count){
                        destinationL = latLng1;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                tempL = new LatLng(location.getLatitude(), location.getLongitude());

                if(tempL!=destinationL){
                    trackingBusReference.child("latlng_schedule").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                tempL_schedule = sourceL;
                                for(DataSnapshot i:snapshot.getChildren()){
                                    double latitude_schedule = (double) i.child("latitude").getValue();
                                    double longitude_schedule = (double) i.child("longitude").getValue();
                                    LatLng tempL1_schedule = new LatLng(latitude_schedule, longitude_schedule);
                                    col=1;
                                    getRoute(tempL_schedule, tempL1_schedule);
                                    tempL_schedule = tempL1_schedule;
                                    mMap.addMarker(new MarkerOptions().position(tempL1_schedule)
                                            .title(busid+"_"+i.getKey())
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempL1_schedule, 15));
                                    int m= Integer.parseInt(i.getKey());
                                    if((myfollowingFlag-1)==m){
                                        //Toast.makeText(UserRoute_GPSActivity.this, "last", Toast.LENGTH_SHORT).show();
                                        tempL1 = tempL1_schedule;
                                    }
                                   // Toast.makeText(UserRoute_GPSActivity.this, ""+i.getKey(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    databaseReference2.child("bus_locations").child(busid).setValue(tempL);
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                    String time = simpleDateFormat.format(date);
                    tempM = mMap.addMarker(new MarkerOptions().position(tempL)
                            .title(busid+"-"+time)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    databaseReference2.child("bus_locations")
                            .child(busid).child("time").setValue(time);
                    hashMap.put(tempL1, tempL);
                    col=1;
                    //getRoute(tempL1, tempL);
                    trackingBusReference.child("latlng_schedule")
                            .child(String.valueOf(myfollowingFlag)).setValue(tempL);
                    myfollowingFlag = myfollowingFlag+1;
                    tempL1 = tempL;

                    int size = flagHashMap.size();

                    //Toast.makeText(UserRoute_GPSActivity.this, "myfollowingFlag"+myfollowingFlag, Toast.LENGTH_SHORT).show();
                    if(Integer.compare(flagInt, size)<0){

                        Location currentL = new Location("A");
                        currentL.setLatitude(tempL.latitude);
                        currentL.setLongitude(tempL.longitude);
                        Location flagToCheck = new Location("B");
                        flagToCheck.setLatitude(flagHashMap.get(flagInt).latitude);
                        flagToCheck.setLongitude(flagHashMap.get(flagInt).longitude);
                        LatLng flatToCheckL = new LatLng(flagToCheck.getLatitude(), flagToCheck.getLongitude());
                        String time1 = startTimeHashMap.get(flatToCheckL);
                        double distance = currentL.distanceTo(flagToCheck);//meter
                        double ideal = 500.0000;//meter
                        Date time2, timeCurrent;
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        try {
                            time2 = sdf.parse(time1);
                            timeCurrent = sdf.parse(time);
                            if(timeCurrent.after(time2)){
                                trackingBusReference.child("time_lag").child("Flag"+flagInt)
                                        .child("time").setValue(
                                                //String.valueOf(timeCurrent)
                                                //        just_time_string
                                                time
                                        );
                                trackingBusReference.child("time_lag").child("Flag"+flagInt)
                                        .child("latlng").setValue(flagHashMap.get(flagInt));
                                if(checkLag==0){
                                    //pop up notification

                                    checkLag=1;
                                }
                                //Toast.makeText(UserRoute_GPSActivity.this, "yes"+flagInt, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(Double.compare(distance, ideal)<0){
                            try {
                                checkLag=1;
                                time2 = sdf.parse(time1);
                                timeCurrent = sdf.parse(time);
                                long difference =(timeCurrent.getTime() - time2.getTime()), difference_In_Minutes, difference_In_Hours;
                                difference_In_Minutes
                                        = (difference
                                        / (1000 * 60))
                                        % 60;
                                difference_In_Hours
                                        = (difference
                                        / (1000 * 60 * 60))
                                        % 24;
                                //Toast.makeText(UserRoute_GPSActivity.this, "d"+distance, Toast.LENGTH_SHORT).show();
                                String flag = String.valueOf(flagInt);
                                mMap.addMarker(new MarkerOptions().position(tempL)
                                        .title(busid+": "+timeCurrent)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                trackingBusReference.child("reaching_schedule")
                                        .child("Flag"+flag).child("reaching_time").setValue(time);
                                //trackingBusReference.child("Flag"+flag).child("time_difference").setValue(String.valueOf(time2+" "+timeCurrent));
                                trackingBusReference.child("reaching_schedule")
                                        .child("Flag"+flag).child("time_difference")
                                        .setValue(String.valueOf(difference_In_Hours*60 + difference_In_Minutes));

                                trackingBusReference.child("Flag"+flag).child("time_difference")
                                        .setValue(String.valueOf(difference_In_Hours*60 + difference_In_Minutes));
                                flagInt=flagInt+1;

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    //check();
                }
                else {
                    Toast.makeText(UserRoute_GPSActivity.this, "Reached Destination", Toast.LENGTH_SHORT).show();
                }
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
            locationManager.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, MIN_TIME, MIN_DIST, (android.location.LocationListener) locationListener);

        }catch (SecurityException e){
            e.printStackTrace();
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