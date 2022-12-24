package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.example.system_for_safe_road.databinding.ActivitySetFlagSetRouteBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetFlag_SetRoute extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private ActivitySetFlagSetRouteBinding binding;
    String source, destination, flag, routeID;
    SearchView searchView;
    LatLng source_latlng, destination_latlng, flag_latlng, last_entered_latlng, source_latlng1;
    HashMap<LatLng, LatLng> sourceDestination = new HashMap<LatLng, LatLng>();
    HashMap<LatLng, String> timer_flag = new HashMap<>();
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    Button cancelBtn, removeBtn, saveBtn, doneBtn, timePickerBtn;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = db.getReference().child("users").child("routes");
    TimePicker timePicker;
    Dialog timer_picker_dialog;
    NumberPicker numberPicker, numberPicker1;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_set_flag_set_route);

        binding = ActivitySetFlagSetRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        source=getIntent().getStringExtra("source");
        destination=getIntent().getStringExtra("dest");
        routeID=getIntent().getStringExtra("routeID");
        searchView = findViewById(R.id.flag_search);
        polylines = new ArrayList<>();
        timer_picker_dialog = new Dialog(this);
        timer_picker_dialog.setCancelable(false);
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

        //Toast.makeText(this, ""+source+destination, Toast.LENGTH_SHORT).show();

        Dialog dialog = new Dialog(this), dialog1 = new Dialog(this);
        dialog.setCancelable(false);
        dialog1.setCancelable(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                flag = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(flag != null || flag.equals("")) {
                    Geocoder geocoder = new Geocoder(SetFlag_SetRoute.this);
                    try {
                        addressList = geocoder.getFromLocationName(flag, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    if(address!=null){
                        flag_latlng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(flag_latlng).title("flag "+flag));
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(flag_latlng, 10));
                        sourceDestination.put(source_latlng, flag_latlng);
                        //Toast.makeText(FlaginRouteActivity.this, ""+sourceDestination.get(source_latlng)+"sl"+flag_latlng, Toast.LENGTH_SHORT).show();
                        source_latlng=last_entered_latlng;
                        getRoute();
                        last_entered_latlng=flag_latlng;

                        if(flag_latlng.equals(destination_latlng)){

                            //dialog1.setContentView(R.layout.flag_timer);
                            dialog.setContentView(R.layout.custom_dialog_box_upon_reaching_destination);
                            //dialog1.show();

                        /*timePicker = dialog1.findViewById(R.id.time_picker);
                        timePicker.setIs24HourView(false);
                        Button button2 = dialog1.findViewById(R.id.timer_ok);
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String s;
                                Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                                Format format = new SimpleDateFormat("hh:mm aa");
                                s= format.format(time);
                                *//*if(timePicker.getCurrentHour()>12){
                                    s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" PM";
                                }
                                else {
                                    s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" AM";
                                }*//*
                                //Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                                timer_flag.put(flag_latlng, s);
                                //Toast.makeText(SetFlag_SetRoute.this, ""+flag_latlng+"time"+s, Toast.LENGTH_SHORT).show();
                                dialog1.dismiss();
                                dialog.show();
                            }
                        });*/

                            timer_picker_dialog.show();
                            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    hour = newVal;
                                }
                            });
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
                                    Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                                    timer_flag.put(flag_latlng, s);
                                    timer_picker_dialog.dismiss();
                                    dialog.show();
                                }
                            });

                            Button btn;
                            btn = dialog.findViewById(R.id.dialog_dismiss);
                            saveBtn = dialog.findViewById(R.id.save_the_route);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save_in_firebase();
                                    dialog.dismiss();
                                }
                            });
                            //Toast.makeText(FlaginRouteActivity.this, "Your Have Reached your destination", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            timer_picker_dialog.show();
                            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    hour = newVal;
                                }
                            });
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
                                    Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                                    timer_flag.put(flag_latlng, s);
                                    timer_picker_dialog.dismiss();
                                }
                            });
                        /*dialog.setContentView(R.layout.flag_timer);
                        dialog.show();
                        timePicker = dialog.findViewById(R.id.time_picker);
                        timePicker.setIs24HourView(false);
                        Button button1 = dialog.findViewById(R.id.timer_ok);
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String s;
                                Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                                Format format = new SimpleDateFormat("hh:mm aa");
                                s= format.format(time);
                                *//*if(timePicker.getCurrentHour()>12){
                                    s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" PM";
                                }
                                else {
                                    s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" AM";
                                }*//*
                                //Toast.makeText(SetFlag_SetRoute.this, ""+flag_latlng+"time"+s, Toast.LENGTH_SHORT).show();
                                timer_flag.put(flag_latlng, s);
                                dialog.dismiss();
                            }
                        });*/

                        }

                    }
                    else {
                        Toast.makeText(SetFlag_SetRoute.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                    }


                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }


    private void save_in_firebase() {

        int hashsize = sourceDestination.size();
        long i=0;
        Toast.makeText(this, ""+hashsize, Toast.LENGTH_SHORT).show();
        databaseReference.child(routeID).removeValue();
        databaseReference.child(routeID).child("source").setValue(source_latlng1);
        databaseReference.child(routeID).child("destination").setValue(destination_latlng);
        LatLng mm = source_latlng1;
        while(i<hashsize){
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("key").setValue(mm);
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("value").setValue(sourceDestination.get(mm));
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("time").setValue(timer_flag.get(sourceDestination.get(mm)));
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("c_index").setValue(i);
            LatLng v = sourceDestination.get(mm);
            mm = v;
            if(v==destination_latlng)
            {
                break;
            }
            i++;
        }
        /*for(LatLng m : sourceDestination.keySet()){
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("key").setValue(m);
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("value").setValue(sourceDestination.get(m));
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("time").setValue(timer_flag.get(sourceDestination.get(m)));
            databaseReference.child(routeID).child("Flag").child("Flag"+i).child("c_index").setValue(i);
            i++;
        }*/

        //databaseReference.child(routeID).updateChildren(sourceDestination);
        /*Map<String, Object> mysourceDest = new HashMap<>();
        for (LatLng m : sourceDestination.keySet()){
            String am = m.toString().trim();
            String bm=sourceDestination.get(m).toString().trim();
            Toast.makeText(this, ""+am, Toast.LENGTH_SHORT).show();
            mysourceDest.put( am , bm);
        }
        databaseReference.child(routeID).push().setValue(mysourceDest);*/
        return;
    }

    private void getRoute() {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(source_latlng, flag_latlng)
                .key("AIzaSyC2QPQUJKkWhPbdNnLCkrCOdxDmRCqim5k")
                .build();
        routing.execute();

        source_latlng=flag_latlng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng flatLng) {
                Toast.makeText(SetFlag_SetRoute.this, ""+flatLng, Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(SetFlag_SetRoute.this);
                dialog.setCancelable(false);
                flag_latlng = flatLng;
                mMap.addMarker(new MarkerOptions().position(flag_latlng).title("flag "+flag));
                sourceDestination.put(source_latlng, flag_latlng);
                source_latlng=last_entered_latlng;
                getRoute();
                last_entered_latlng=flag_latlng;

                if(flag_latlng.equals(destination_latlng)){
                    dialog.setContentView(R.layout.custom_dialog_box_upon_reaching_destination);
                    timer_picker_dialog.show();
                    numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            hour = newVal;
                        }
                    });
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
                            String s = Integer.toString(minute);
                            Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                            timer_flag.put(flag_latlng, s);
                            timer_picker_dialog.dismiss();
                            dialog.show();
                        }
                    });

                    Button btn;
                    btn = dialog.findViewById(R.id.dialog_dismiss);
                    saveBtn = dialog.findViewById(R.id.save_the_route);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save_in_firebase();
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    timer_picker_dialog.show();
                    numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            hour = newVal;
                        }
                    });
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
                            String s = Integer.toString(minute);
                            Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                            timer_flag.put(flag_latlng, s);
                            timer_picker_dialog.dismiss();
                        }
                    });

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LatLng latLng1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

                if(latLng1.equals(destination_latlng) || latLng1.equals(source_latlng1)){
                    int i=0;
                    if(latLng1.equals(destination_latlng)){
                        for(LatLng ab : sourceDestination.keySet()){

                            if(sourceDestination.get(ab).equals(destination_latlng)){
                                i=0;
                                Dialog dialog5 = new Dialog(SetFlag_SetRoute.this);
                                dialog5.setCancelable(false);
                                dialog5.setContentView(R.layout.custom_dialog_box_upon_reaching_destination);
                                dialog5.show();
                                saveBtn=dialog5.findViewById(R.id.save_the_route);
                                saveBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        save_in_firebase();
                                        dialog5.dismiss();
                                    }
                                });
                                doneBtn=dialog5.findViewById(R.id.dialog_dismiss);
                                doneBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog5.dismiss();
                                    }
                                });
                                break;
                                /*Toast.makeText(SetFlag_SetRoute.this, "go", Toast.LENGTH_SHORT).show();
                                break;*/
                            }
                            else {
                                i=1;
                            }
                        }
                        if(i==1){
                            Toast.makeText(SetFlag_SetRoute.this, "Can't be removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(SetFlag_SetRoute.this, "Can't be removed", Toast.LENGTH_SHORT).show();
                    }

                }
                else {

                    Dialog del_timer = new Dialog(SetFlag_SetRoute.this);
                    del_timer.setCancelable(true);
                    del_timer.setContentView(R.layout.delete_or_timer);
                    del_timer.show();
                    Button rmvBtn, timerBtn;
                    rmvBtn = del_timer.findViewById(R.id.removeButtonDialog);
                    timerBtn = del_timer.findViewById(R.id.timerButtonDialog);

                    timerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            del_timer.dismiss();
                            timer_picker_dialog.show();
                            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    hour = newVal;
                                }
                            });
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
                                    Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                                    timer_flag.remove(latLng1);
                                    timer_flag.put(latLng1, s);
                                    timer_picker_dialog.dismiss();
                                }
                            });
                            /*Dialog dialog3 = new Dialog(SetFlag_SetRoute.this);
                            dialog3.setCancelable(false);
                            dialog3.setContentView(R.layout.flag_timer);
                            dialog3.show();

                            timePicker = dialog3.findViewById(R.id.time_picker);
                            timePicker.setIs24HourView(false);
                            Button button2 = dialog3.findViewById(R.id.timer_ok);
                            button2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String s;
                                    Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                                    Format format = new SimpleDateFormat("hh:mm aa");
                                    s= format.format(time);
                                    *//*if(timePicker.getCurrentHour()>12){
                                        s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" PM";
                                    }
                                    else {
                                        s = timePicker.getCurrentHour().toString()+':'+timePicker.getCurrentMinute().toString()+" AM";
                                    }*//*
                                    //Toast.makeText(SetFlag_SetRoute.this, ""+s, Toast.LENGTH_SHORT).show();
                                    timer_flag.remove(latLng1);
                                    timer_flag.put(latLng1, s);
                                    dialog3.dismiss();
                                }
                            });*/

                        }
                    });

                    rmvBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            del_timer.dismiss();
                            Dialog dialog1 = new Dialog(SetFlag_SetRoute.this);
                            dialog1.setCancelable(false);
                            dialog1.setContentView(R.layout.deleting_marker);
                            dialog1.show();
                            cancelBtn = dialog1.findViewById(R.id.cancel);
                            removeBtn = dialog1.findViewById(R.id.remove);
                            TextView addressE = dialog1.findViewById(R.id.marker_address);
                            TextView cityE = dialog1.findViewById(R.id.marker_city);
                            Geocoder geocoder = new Geocoder(SetFlag_SetRoute.this, Locale.getDefault());
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
                                    eraseLine();

                                    if(latLng1.equals(last_entered_latlng)){

                                        for(LatLng m : sourceDestination.keySet()){
                                            if (sourceDestination.get(m).equals(latLng1)){
                                                last_entered_latlng=m;
                                                sourceDestination.remove(m);
                                                break;
                                            }
                                        }

                                    }
                                    else {
                                        for(LatLng i : sourceDestination.keySet()){
                                            if(sourceDestination.get(i).equals(latLng1)){
                                                //Toast.makeText(SetFlag_SetRoute.this, "bliblablu1", Toast.LENGTH_SHORT).show();
                                                source_latlng=i;
                                                flag_latlng=sourceDestination.get(latLng1);
                                                sourceDestination.put(i, sourceDestination.get(latLng1));
                                                sourceDestination.remove(latLng1);
                                                break;
                                            }
                                        }
                                    }
                                    for(LatLng m : sourceDestination.keySet()){
                                        source_latlng=m;
                                        flag_latlng=sourceDestination.get(m);
                                        getRoute();

                                    }
                                    marker.remove();
                                    //getRoute();
                                    dialog1.dismiss();
                                }
                            });
                        }
                    });



                }

                return false;
            }
        });
        if(source != null || source.equals("")){
            List<Address> addressList = null;
            Geocoder geocoder = new Geocoder(SetFlag_SetRoute.this);
            try {
                addressList = geocoder.getFromLocationName(source, 1);
            } catch (IOException e){
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            source_latlng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(source_latlng).title("Source"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_latlng, 10));
            last_entered_latlng=source_latlng;
            source_latlng1=source_latlng;

        }
        if(destination != null || destination.equals("")){
            List<Address> addressList = null;
            Geocoder geocoder = new Geocoder(SetFlag_SetRoute.this);
            try {
                addressList = geocoder.getFromLocationName(destination, 1);
            } catch (IOException e){
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            destination_latlng = new LatLng(address.getLatitude(), address.getLongitude());
            //Toast.makeText(this, ""+address.getLatitude()+address.getLongitude(), Toast.LENGTH_SHORT).show();
            mMap.addMarker(new MarkerOptions().position(destination_latlng).title("Destination"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination_latlng, 10));

        }
    }

    private void eraseLine() {
        for(Polyline line : polylines){
            line.remove();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortest) {
        for (int i = 0; i <arrayList.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(arrayList.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

}