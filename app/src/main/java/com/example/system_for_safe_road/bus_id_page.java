package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class bus_id_page extends AppCompatActivity {
    EditText bus;
    Button busBtn;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_id_page);
        getSupportActionBar().hide();

        drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.user_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        bus = findViewById(R.id.busIdEdit);
        busBtn = findViewById(R.id.confirm_button1);
        busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busId = bus.getText().toString();
                Intent intent = new Intent(bus_id_page.this, User_Home_Activity.class);
                intent.putExtra("busid", busId);
                startActivity(intent);
            }
        });
    }
}