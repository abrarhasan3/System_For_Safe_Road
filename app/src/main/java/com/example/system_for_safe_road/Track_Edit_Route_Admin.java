package com.example.system_for_safe_road;

import static android.provider.Contacts.SettingsColumns.KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Track_Edit_Route_Admin extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    String RouteId;
    private GoogleMap mMap;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    protected LatLng start = null;
    protected LatLng end = null;
    private List<Polyline> polylines = null;
    DatabaseReference reference ;
    long index_start = 0 , index_end;

    ArrayList<customArrayList_for_all_route> flags ;

    int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit_route_admin);


        flags = new ArrayList<>();
        RouteId = getIntent().getStringExtra("RouteId");
        reference = FirebaseDatabase.getInstance().getReference().child("users").child("routes").child(RouteId);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.all_Route_Map);
        mapFragment.getMapAsync(this);


    }

    public void Findroutes()
    {

            for(int i =0;i<flags.size();i++)
            {
                start = flags.get(i).getStart();
                end = flags.get(i).getEnd();
                index_start = flags.get(i).getIndex();

                Log.println(Log.ERROR,"index Val", ""+index_start);

                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(start, end)
                        .key("AIzaSyC2QPQUJKkWhPbdNnLCkrCOdxDmRCqim5k")  //also define your api key here.
                        .build();
                routing.execute();
            }
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(flags.get(flags.size()-1).getEnd());
        Marker m1 = mMap.addMarker(endMarker);
         m1.setTag(flags.get(flags.size()-1).getIndex());

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(android.R.color.holo_red_dark));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polyline.setClickable(true);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);
            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);

        Marker m = mMap.addMarker(startMarker);
        Location temp1 = new Location("StartPolyline");
        temp1.setLatitude(polylineStartLatLng.latitude);
        temp1.setLongitude(polylineStartLatLng.longitude);

        Location temp3 = new Location("ENDPolyline");
        temp3.setLatitude(polylineEndLatLng.latitude);
        temp3.setLongitude(polylineEndLatLng.longitude);

        Log.println(Log.ERROR,"D",""+flags.size());


        for(int i=0;i<flags.size();i++)
        {
            Location temp2= new Location("ActualStart");
            temp2.setLatitude(flags.get(i).getStart().latitude);
            temp2.setLongitude(flags.get(i).getStart().longitude);

            Location temp4= new Location("ActualEnd");
            temp4.setLatitude(flags.get(i).getEnd().latitude);
            temp4.setLongitude(flags.get(i).getEnd().longitude);

            double distance = temp1.distanceTo(temp2);
            double distance2 = temp3.distanceTo(temp4);
            Log.println(Log.ERROR,"DIFF",""+i +"  1: "+distance+"   2:"+distance2 );
            if(distance<1000 && distance2<1000)
            {
                m.setTag(flags.get(i).getIndex());
                break;
            }

        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polylineEndLatLng, 10));




        //Add Marker on route ending position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(polylineEndLatLng);
//        mMap.addMarker(endMarker);


    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.child("Flag").getChildren())
                {
                    Double a = (double)dataSnapshot.child("key").child("latitude").getValue();
                    Double b = (double)dataSnapshot.child("key").child("longitude").getValue();
                    index_start =(long) dataSnapshot.child("c_index").getValue();
                    Double a1 = (double)dataSnapshot.child("value").child("latitude").getValue();
                    Double b1 = (double)dataSnapshot.child("value").child("longitude").getValue();

                    customArrayList_for_all_route temp=new customArrayList_for_all_route(new LatLng(a,b),new LatLng(a1,b1),index_start);
                    flags.add(temp);
                }
                //Toast.makeText(Track_Edit_Route_Admin.this,""+flags.size(),Toast.LENGTH_SHORT).show();
                Findroutes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.println(Log.ERROR,"Database",error.toString());

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                polyline.remove();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                long a =(long)marker.getTag();
                Log.println(Log.ERROR,"LATLANG",""+a);

                Toast.makeText(Track_Edit_Route_Admin.this,""+a+"  "+marker.getPosition().latitude, Toast.LENGTH_SHORT).show();
                return false;
            }
        });






    }
}