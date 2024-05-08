package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class help extends AppCompatActivity {
    private EditText helpNameEditText;
    private EditText helpPhoneEditText;
    private EditText helpLocationEditText;
    private EditText helpDetailsEditText;
    private Button helpSaveButton;
    private Button helpCallButton;

    private DatabaseReference helpReference;

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Get reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        helpReference = database.getReference("admin_help");

        // Get references to the views
        helpNameEditText = findViewById(R.id.helpNameEditText);
        helpPhoneEditText = findViewById(R.id.helpPhoneEditText);
        helpLocationEditText = findViewById(R.id.helpLocationEditText);
        helpDetailsEditText = findViewById(R.id.helpDetailsEditText);
        helpSaveButton = findViewById(R.id.helpSaveButton);
        helpCallButton = findViewById(R.id.helpCallButton);

        helpSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHelpRequest();
            }
        });

        helpCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "8431224909";  // Set the phone number to be dialed
                makePhoneCall(phoneNumber);
            }
        });
    }

    private void saveHelpRequest() {
        // Get the input values
        String name = helpNameEditText.getText().toString();
        String phone = helpPhoneEditText.getText().toString();
        String location = helpLocationEditText.getText().toString();
        String details = helpDetailsEditText.getText().toString();

        // Create a new HelpRequest object
        HelpRequest helpRequest = new HelpRequest(name, phone, location, details);

        // Save the help request data to Firebase Realtime Database
        helpReference.push().setValue(helpRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data saved successfully
                        Toast.makeText(help.this, "Help request saved", Toast.LENGTH_SHORT).show();

                        // Check if the app has call permission
                        if (ContextCompat.checkSelfPermission(help.this, Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            // Permission is granted, make the phone call
                            makePhoneCall(phone);
                        } else {
                            // Request the permission
                            ActivityCompat.requestPermissions(help.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    REQUEST_PERMISSIONS);
                        }

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(help.this, "Failed to save help request", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    // Method to make a phone call
    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        // Check if the CALL_PHONE permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // Check if the phone number is the specific number (8431224909)
            if (phoneNumber.equals("8431224909")) {
                // Permission is granted and the phone number is the specific number, make the phone call
                startActivity(intent);
            } else {
                // Show a message that the phone number is not allowed
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_PERMISSIONS);
        }
    }

    // Override onRequestPermissionsResult to handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, make the phone call
                String phone = helpPhoneEditText.getText().toString();
                makePhoneCall(phone);
            } else {
                // Permission is denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
