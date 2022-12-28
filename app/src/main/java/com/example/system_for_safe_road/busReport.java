package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class busReport extends AppCompatActivity {

    String busID;
    DatabaseReference reference;
    String flag,atime,delay,rTime;
    int isDelay;
    public List<customClassR> userlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_report);

        reference= FirebaseDatabase.getInstance().getReference().child("users");

        createAlertD();

    }


    public void createAlertD()
    {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(busReport.this);
        alertDialog.setTitle("WELCOME TO BUS TRACKING!");
        alertDialog.setMessage("Enter the bus id:");
        final EditText alart= new EditText(busReport.this);
        alart.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(alart);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                busID=alart.getText().toString().trim();
                fetchdata();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    public void fetchdata()
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("trips").child("routeID").hasChild(busID) == false)
                {
                    AlertDialog.Builder alertDialog1= new AlertDialog.Builder(busReport.this);
                    alertDialog1.setTitle("No data found! Sorry!");
                    alertDialog1.setPositiveButton("Search Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            createAlertD();

                        }
                    });
                    alertDialog1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog1.show();

                }
                else {
                    String routeId = snapshot.child("trips").child("routeID").child(busID).getValue().toString();

                    for (DataSnapshot dataSnapshot : snapshot.child("routes").child(routeId).child("Flag").getChildren()) {

                        flag = dataSnapshot.getKey();
                        atime = dataSnapshot.child("time").getValue().toString();
                        if (snapshot.child("track").child(busID).child(routeId).child("reaching_schedule").hasChild(flag)) {
                            rTime = snapshot.child("track").child(busID).child(routeId).child("reaching_schedule").child(flag).child("reaching_time").getValue().toString();
                            String delayT = snapshot.child("track").child(busID).child(routeId).child("reaching_schedule").child(flag).child("time_difference").getValue().toString();
                            if (delayT.charAt(0) == '-') {
                                delay = "No Delay";
                                isDelay = 0;
                            } else {

                                delay = delayT +" Minute";
                                isDelay = 1;
                            }
                        } else {
                            rTime = "NOT YET REACHED";
                            delay = "Not Available";
                        }
                        initData();

                    }


                    initRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(busReport.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void initData() {

        //userlist.add(new Modelclassfor_recycle(price1,name1,changePercent,ticker));


        userlist.add(new customClassR(flag,atime,delay,rTime,isDelay));
    }

    private void initRecyclerView() {
        RecyclerView recyclerView;
        LinearLayoutManager linearLayoutManager;
        Adapter adapter;
        recyclerView = findViewById(R.id.recycleview_report);
        linearLayoutManager = new LinearLayoutManager(busReport.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Adapter(userlist, busReport.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}