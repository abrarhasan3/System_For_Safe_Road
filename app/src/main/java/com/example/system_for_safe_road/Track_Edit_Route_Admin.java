package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit_route_admin);



        RouteId = getIntent().getStringExtra("RouteId");
        reference = FirebaseDatabase.getInstance().getReference().child("users").child("routes").child(RouteId);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.all_Route_Map);
        mapFragment.getMapAsync(this);


    }

    public void Findroutes()
    {
        if(start==null || end==null) {
            Toast.makeText(Track_Edit_Route_Admin.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .key("AIzaSyC2QPQUJKkWhPbdNnLCkrCOdxDmRCqim5k")  //also define your api key here.
                    .build();
            routing.execute();
        }
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
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
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
                    start = new LatLng(a,b);
                     a = (double)dataSnapshot.child("value").child("latitude").getValue();
                     b = (double)dataSnapshot.child("value").child("longitude").getValue();
                     end = new LatLng(a,b);
                    Findroutes();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
}