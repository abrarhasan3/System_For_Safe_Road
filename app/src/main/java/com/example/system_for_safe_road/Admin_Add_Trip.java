package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Admin_Add_Trip extends AppCompatActivity {
    ArrayList<String> stringList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    Button enterBtn, timedoneBtn;
    EditText bus_id_txt;
    String routeNum;
    Dialog dialog;
    TextView startTimeView;
    TimePicker timePicker;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_trip);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        enterBtn = findViewById(R.id.entering_trip);
        bus_id_txt = findViewById(R.id.entering_bus_id);
        startTimeView = findViewById(R.id.start_time_id);
        dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.flag_timer);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.child("routes").getChildren()){
                    String s = dataSnapshot.getKey();
                    //Toast.makeText(Admin_Add_Trip.this, ""+s, Toast.LENGTH_SHORT).show();
                    stringList.add(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        arrayAdapter = new ArrayAdapter<>(Admin_Add_Trip.this, R.layout.route_list, stringList);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                routeNum = parent.getItemAtPosition(position).toString();
            }
        });

        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                timePicker = dialog.findViewById(R.id.time_picker);
                timePicker.setIs24HourView(false);
                timedoneBtn = dialog.findViewById(R.id.timer_ok);
                timedoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Time time = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        Format format = new SimpleDateFormat("hh:mm aa");
                        s= format.format(time);
                        startTimeView.setText(s);
                        dialog.dismiss();
                    }
                });

            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bus_id = bus_id_txt.getText().toString();
                if(routeNum.equals("") || bus_id.equals("") || s.equals("")){
                    Toast.makeText(Admin_Add_Trip.this, "Fill in all the details", Toast.LENGTH_SHORT).show();
                }
                else {

                    //Toast.makeText(Admin_Add_Trip.this, ""+routeNum, Toast.LENGTH_SHORT).show();
                    databaseReference.child("trips").child("routeID").child(bus_id).setValue(routeNum);
                    databaseReference.child("trips").child("startTime").child(bus_id).setValue(s);
                    Toast.makeText(Admin_Add_Trip.this, "Trip added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Admin_Add_Trip.this, adminPannel.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Admin_Add_Trip.this, adminPannel.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}