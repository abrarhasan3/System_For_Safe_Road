package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class All_Routes_Activity extends AppCompatActivity {

   DatabaseReference reference_route;
   AutoCompleteTextView autoCompleteTextView;
   ArrayList<String> stringList = new ArrayList<>();
   ArrayAdapter<String> arrayAdapter;
   Button confirm_button;
   String pass_to_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);

        reference_route= FirebaseDatabase.getInstance().getReference().child("users").child("routes");
        autoCompleteTextView=findViewById(R.id.auto_complete_txt_all_routes);
        confirm_button= findViewById(R.id.confirm_button_allroutes);

        preset();
    }

    public void preset()
    {
        reference_route.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String s = dataSnapshot.getKey();
                    stringList.add(s);
                    arrayAdapter = new ArrayAdapter<>(All_Routes_Activity.this, R.layout.route_list, stringList);
                    autoCompleteTextView.setAdapter(arrayAdapter);
                }

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        pass_to_next = adapterView.getItemAtPosition(i).toString();
                    }
                });

                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(All_Routes_Activity.this, Track_Edit_Route_Admin.class);
                        intent.putExtra("RouteId", pass_to_next);
                        startActivity(intent);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}