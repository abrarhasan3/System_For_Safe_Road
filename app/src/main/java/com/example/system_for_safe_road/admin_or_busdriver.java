package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class admin_or_busdriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_or_busdriver);
        LinearLayout layout1 =findViewById(R.id.admin);
        LinearLayout layout2 =findViewById(R.id.busdriver);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_or_busdriver.this,admin_home_temporary.class);
                startActivity(intent);
                finish();
                //Toast.makeText(admin_or_busdriver.this,"HI",Toast.LENGTH_SHORT).show();
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_or_busdriver.this,bus_id_page.class);
                startActivity(intent);
                finish();
            }
        });
    }



}