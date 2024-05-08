package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin_help extends AppCompatActivity {

    private TextView displayNameTextView;
    private TextView displayPhoneTextView;
    private TextView displayLocationTextView;
    private TextView displayDetailsTextView;

    private DatabaseReference helpReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_help);

        displayNameTextView = findViewById(R.id.displayNameTextView);
        displayPhoneTextView = findViewById(R.id.displayPhoneTextView);
        displayLocationTextView = findViewById(R.id.displayLocationTextView);
        displayDetailsTextView = findViewById(R.id.displayDetailsTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String phone = extras.getString("phone");
            String location = extras.getString("location");
            String details = extras.getString("details");

            displayNameTextView.setText("Name: " + name);
            displayPhoneTextView.setText("Phone: " + phone);
            displayLocationTextView.setText("Location: " + location);
            displayDetailsTextView.setText("Details: " + details);
        }

        // Retrieve additional data from Firebase Realtime Database
        helpReference = FirebaseDatabase.getInstance().getReference("admin_help");
        helpReference.child("additionalData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String additionalData = dataSnapshot.getValue(String.class);
                    displayDetailsTextView.append("\nAdditional Data: " + additionalData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
