package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Volunteer2Activity extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewAge;
    private TextView textViewPhoneNumber;
    private TextView textViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer2);
        textViewName = findViewById(R.id.textViewName);
        textViewAge = findViewById(R.id.textViewAge);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewAddress = findViewById(R.id.textViewAddress);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String age = extras.getString("age");
            String phoneNumber = extras.getString("phoneNumber");
            String address = extras.getString("address");

            // Display the form data
            textViewName.setText("Name: " + name);
            textViewAge.setText("Age: " + age);
            textViewPhoneNumber.setText("Phone Number: " + phoneNumber);
            textViewAddress.setText("Address: " + address);
        }
    }
}





