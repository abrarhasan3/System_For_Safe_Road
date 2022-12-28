package com.example.system_for_safe_road;

import static android.provider.Contacts.SettingsColumns.KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SearchView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    Dialog timer_picker_dialog;
    NumberPicker numberPicker,numberPicker1;
    Button timePickerBtn;
    int hour, minute;
    HashMap<Long,customArrayList_for_all_route> flag_hash;

    int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit_route_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        flags = new ArrayList<>();
        flag_hash = new HashMap<>();
        RouteId = getIntent().getStringExtra("RouteId");
        reference = FirebaseDatabase.getInstance().getReference().child("users").child("routes").child(RouteId);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.all_Route_Map);
        mapFragment.getMapAsync(this);


        timer_picker_dialog = new Dialog(this);
        timer_picker_dialog.setCancelable(true);
        timer_picker_dialog.setContentView(R.layout.time_picker);
        numberPicker=timer_picker_dialog.findViewById(R.id.hour_id);
        numberPicker1=timer_picker_dialog.findViewById(R.id.minute_id);
        timePickerBtn=timer_picker_dialog.findViewById(R.id.set_timer);
        numberPicker.setMinValue(00);
        numberPicker.setMaxValue(99);
        numberPicker1.setMinValue(00);
        numberPicker1.setMaxValue(59);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.70);
        timer_picker_dialog.getWindow().setLayout(width, height);




    }

    public  void Fetchdatabase()
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.child("Flag").getChildren())
                {
                    Double a = (double)dataSnapshot.child("key").child("latitude").getValue();
                    Double b = (double)dataSnapshot.child("key").child("longitude").getValue();
                    index_start =(long) dataSnapshot.child("c_index").getValue();
                    long next = (long)dataSnapshot.child("n_index").getValue();
                    long prev = (long)dataSnapshot.child("p_index").getValue();

                    Double a1 = (double)dataSnapshot.child("value").child("latitude").getValue();
                    Double b1 = (double)dataSnapshot.child("value").child("longitude").getValue();

                    customArrayList_for_all_route temp=new customArrayList_for_all_route(new LatLng(a,b),new LatLng(a1,b1),index_start,next,prev);
                    flags.add(temp);
                    flag_hash.put(index_start,temp);

                }
                //Toast.makeText(Track_Edit_Route_Admin.this,""+flags.size(),Toast.LENGTH_SHORT).show();
                Findroutes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.println(Log.ERROR,"Database",error.toString());

            }
        });
    }

    private void eraseLine() {
        for(Polyline line : polylines){
            line.remove();
        }
    }
    public void Findroutes()
    {
        //eraseLine();
            for(int i =0;i<flags.size();i++)
            {
                start = flags.get(i).getStart();
                end = flags.get(i).getEnd();
                if(flag_hash.get(flags.get(i).getNext()) != null)
                {
                    end=flag_hash.get(flags.get(i).getNext()).getStart();
                }
                else
                {
                    Log.println(Log.ERROR,"Hash Map Null","NULL");
                }

                //for()
//                for(int j=0;i< flags.size();i++)
//                {
//                    Log.println(Log.ERROR, "TT","1: "+flags.get(i).getNext() +"  2:"+flags.get(j).getIndex() );
////                    if(flags.get(i).getNext() == )
////                    {
////                        end = flags.get(j).getStart();
////                    }
//
//                }
                Log.println(Log.ERROR,"END 2", ""+end);
                index_start = flags.get(i).getIndex();



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

         m1.setTag(flags.get(flags.size()-1));

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
            if(distance<100)
            {
                m.setTag(flags.get(i));
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
        Fetchdatabase();



        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                polyline.remove();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                customArrayList_for_all_route markar_info =(customArrayList_for_all_route) marker.getTag();


                Dialog editdialog = new Dialog(Track_Edit_Route_Admin.this);
                editdialog.setCancelable(true);
                editdialog.setContentView(R.layout.customdialog_rm_time_edit);
                editdialog.show();
                Button rmvBtn, timerBtn, editFlagLocation;
                rmvBtn = editdialog.findViewById(R.id.removeButtonDialog);
                timerBtn = editdialog.findViewById(R.id.timerButtonDialog);
                editFlagLocation = editdialog.findViewById(R.id.edit_Location);

                timerBtn.setText("EDIT TIME ?");
                rmvBtn.setText("DELETE");



                timerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editdialog.dismiss();
                        timer_picker_dialog.show();

                        numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                minute = newVal;
                            }
                        });
                        timePickerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hour = hour * 60;
                                minute = minute + hour;
                                //String sh = Integer.toString(hour), sm=Integer.toString(minute);
                                //String s = sh+':'+sm;
                                String s = Integer.toString(minute);
                                Toast.makeText(Track_Edit_Route_Admin.this, ""+s, Toast.LENGTH_SHORT).show();
                                reference.child("Flag").child("Flag"+markar_info.getIndex()).child("time").setValue(s);
                                timer_picker_dialog.dismiss();
                            }
                        });
                    }
                });

                rmvBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editdialog.dismiss();
                        //Log.println(Log.ERROR,"A","HI");
                        Dialog dialog1 = new Dialog(Track_Edit_Route_Admin.this);
                        dialog1.setCancelable(false);
                        dialog1.setContentView(R.layout.deleting_marker);
                        dialog1.show();
                        Button cancelBtn = dialog1.findViewById(R.id.cancel);
                        Button removeBtn = dialog1.findViewById(R.id.remove);
                        TextView addressE = dialog1.findViewById(R.id.marker_address);
                        TextView cityE = dialog1.findViewById(R.id.marker_city);
                        Geocoder geocoder = new Geocoder(Track_Edit_Route_Admin.this, Locale.getDefault());
                        List<Address> addressList = null;
                        try {
                            addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                            addressE.setText(addressList.get(0).getAddressLine(0));
                            cityE.setText(addressList.get(0).getLocality());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        removeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child("Flag").child("Flag"+markar_info.getPrev()).child("n_index").setValue(markar_info.getNext());
                                reference.child("Flag").child("Flag"+markar_info.getNext()).child("p_index").setValue(markar_info.getPrev());
                                reference.child("Flag").child("Flag"+markar_info.getIndex()).removeValue();
                                marker.remove();
                                eraseLine();
                                polylines.clear();
                                flags.clear();
                                Fetchdatabase();
                                dialog1.dismiss();
                            }
                        });
                    }
                });

                editFlagLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        editdialog.dismiss();
                        Dialog dialog11 = new Dialog(Track_Edit_Route_Admin.this);
                        dialog11.setCancelable(true);
                        dialog11.setContentView(R.layout.search_dialog);

                        dialog11.show();

                        SearchView searchView = dialog11.findViewById(R.id.edit_Flag);

                        Button button =dialog11.findViewById(R.id.cancel_button_search);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog11.dismiss();
                            }
                        });

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                dialog11.dismiss();
                                String new_flag = searchView.getQuery().toString();

                                List<Address> addressList = null;
                                if (new_flag != null || new_flag.equals("")) {
                                    Geocoder geocoder = new Geocoder(Track_Edit_Route_Admin.this);
                                    try {
                                        addressList = geocoder.getFromLocationName(new_flag, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Address address = addressList.get(0);
                                    LatLng flag_latlng = new LatLng(address.getLatitude(), address.getLongitude());
                                    Log.println(Log.ERROR,"ERR",""+flag_latlng);
                                    reference.child("Flag").child("Flag" + markar_info.getIndex()).child("key").child("latitude").setValue(address.getLatitude());
                                    reference.child("Flag").child("Flag" + markar_info.getIndex()).child("key").child("longitude").setValue(address.getLongitude());
                                    //mMap.addMarker(new MarkerOptions().position(flag_latlng));
                                    marker.remove();
                                    eraseLine();
                                    polylines.clear();
                                    flags.clear();
                                    Fetchdatabase();

                                }

//                                timer_picker_dialog.show();
//                                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                                    @Override
//                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                                        hour = newVal;
//                                    }
//                                });
//                                numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                                    @Override
//                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                                        minute = newVal;
//                                    }
//                                });
//                                timePickerBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        hour = hour * 60;
//                                        minute = minute + hour;
//                                        String s = Integer.toString(minute);
//                                        timer_picker_dialog.dismiss();
//                                        reference.child("Flag").child("Flag" + markar_info.getIndex()).child("time").setValue(s);
//                                    }
//                                });
                                return false;
                            }
                            @Override
                            public boolean onQueryTextChange(String s) {
                                return false;
                            }
                        });
                    }
                });



                return false;
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Track_Edit_Route_Admin.this, adminPannel.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}