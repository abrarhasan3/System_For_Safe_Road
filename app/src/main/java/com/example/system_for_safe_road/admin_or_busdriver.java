package com.example.system_for_safe_road;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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

                AlertDialog.Builder alertDialog= new AlertDialog.Builder(admin_or_busdriver.this);
                alertDialog.setTitle("WELCOME TO ADMIN PANEL!");
                alertDialog.setMessage("Please Enter Your Password:");
                final EditText alart= new EditText(admin_or_busdriver.this);
                alart.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                alertDialog.setView(alart);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = alart.getText().toString().trim();
                        if( a.equals("admin"))
                        {
                            Intent intent = new Intent(admin_or_busdriver.this,adminPannel.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(admin_or_busdriver.this,"Wrong Password",Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }

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
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(admin_or_busdriver.this,BusId_or_Classification.class);
                startActivity(intent);
                finish();
            }
        });
    }



}