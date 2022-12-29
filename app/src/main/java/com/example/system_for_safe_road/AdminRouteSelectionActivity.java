package com.example.system_for_safe_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminRouteSelectionActivity extends AppCompatActivity {
    TextInputEditText SourceInputEditText, DestinationInputEditText, RouteInputEditText;
    String SourceString, DestinationString, RouteString;
    Button Enter;
    TextInputLayout SourceLayout, DestinationLayout, RouteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_route_selection);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SourceInputEditText=findViewById(R.id.source_text);
        DestinationInputEditText=findViewById(R.id.destination_text);

        Enter=findViewById(R.id.route_accept);

        SourceLayout=findViewById(R.id.source_layout);
        DestinationLayout=findViewById(R.id.destination_layout);
        RouteLayout=findViewById(R.id.routeID_layout);


        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceString=SourceLayout.getEditText().getText().toString().trim();
                DestinationString=DestinationLayout.getEditText().getText().toString().trim();
                RouteString=RouteLayout.getEditText().getText().toString().trim();
                if(SourceString.equals("")||DestinationString.equals("")||RouteString.equals("")){
                    Toast.makeText(AdminRouteSelectionActivity.this, "Enter all the values", Toast.LENGTH_SHORT).show();

                }
                else {
                    Intent intent=new Intent(AdminRouteSelectionActivity.this, SetFlag_SetRoute.class);
                    intent.putExtra("source", SourceString);
                    intent.putExtra("dest", DestinationString);
                    intent.putExtra("routeID", RouteString);
                    //Toast.makeText(AdminRouteSelectionActivity.this, ""+SourceString+DestinationString, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AdminRouteSelectionActivity.this, adminPannel.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}