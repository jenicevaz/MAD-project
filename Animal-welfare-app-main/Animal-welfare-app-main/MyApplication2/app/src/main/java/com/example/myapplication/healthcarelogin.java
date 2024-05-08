package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class healthcarelogin extends AppCompatActivity {
    EditText usernames;
    EditText password;
    Button Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthcarelogin);

        usernames = findViewById(R.id.username);
        password = findViewById(R.id.PASSWORD1);
        Submit = findViewById(R.id.SUBMIT1);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernames.getText().toString();
                String passwordStr = password.getText().toString();

                if (username.equals("esha") && passwordStr.equals("esha")) {
                    Intent intent = new Intent(healthcarelogin.this, adminHome.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
