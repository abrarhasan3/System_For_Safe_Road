package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class adminPannel extends AppCompatActivity {

    int selected=0,index=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pannel);
        ActionBar actionBar = getSupportActionBar();


        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        preset();
        preset1();
        preset2();

        Button button = findViewById(R.id.confirm_button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==0)
                {
                    Toast.makeText(adminPannel.this,"No choice Selected", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(index == 1)

                    {
                        Intent intent = new Intent(adminPannel.this, AdminRouteSelectionActivity.class);
                        startActivity(intent);
                    }
                    else if(index == 2)

                    {
                        Intent intent = new Intent(adminPannel.this, Admin_Add_Trip.class);
                        startActivity(intent);
                    }
                    else if(index == 3)

                    {
                        Intent intent = new Intent(adminPannel.this, bus_id_for_tracking.class);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    private void preset() {
        LinearLayout l=findViewById(R.id.routeSelect);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==0) {
                    l.setBackgroundColor(Color.parseColor("#05a85c"));
                    Button button = findViewById(R.id.confirm_button2);
                    button.setVisibility(View.VISIBLE);
                    selected=1;
                    index=1;
                }
                else{
                    if(index==1) {
                        l.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        Button button = findViewById(R.id.confirm_button2);
                        button.setVisibility(View.GONE);
                        selected = 0;
                        index = -1;
                    }
                    else{
                        if(index==2)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.addNew);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 1;
                        }
                        else if(index==3)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.track);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 1;
                        }
                    }

                }
            }
        });
    }
    private void preset1() {
        LinearLayout l=findViewById(R.id.addNew);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==0) {
                    l.setBackgroundColor(Color.parseColor("#05a85c"));
                    Button button = findViewById(R.id.confirm_button2);
                    button.setVisibility(View.VISIBLE);
                    selected=1;
                    index=2;
                }
                else{
                    if(index==2) {
                        l.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        Button button = findViewById(R.id.confirm_button2);
                        button.setVisibility(View.GONE);
                        selected = 0;
                        index = -1;
                    }
                    else{
                        if(index==1)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.routeSelect);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 2;
                        }
                        else if(index==3)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.track);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 2;
                        }
                    }

                }
            }
        });
    }
    private void preset2() {
        LinearLayout l=findViewById(R.id.track);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==0) {
                    l.setBackgroundColor(Color.parseColor("#05a85c"));
                    Button button = findViewById(R.id.confirm_button2);
                    button.setVisibility(View.VISIBLE);
                    selected=1;
                    index=3;
                }
                else{
                    if(index==3) {
                        l.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        Button button = findViewById(R.id.confirm_button2);
                        button.setVisibility(View.GONE);
                        selected = 0;
                        index = -1;
                    }
                    else{
                        if(index==1)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.routeSelect);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 3;
                        }
                        else if(index==2)
                        {
                            l.setBackgroundColor(Color.parseColor("#05a85c"));
                            LinearLayout l1=findViewById(R.id.addNew);
                            l1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            index = 3;
                        }
                    }

                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(adminPannel.this, admin_or_busdriver.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}