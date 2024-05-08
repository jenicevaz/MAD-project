package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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

public class PetFoodOrderActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static final String PHONE_NUMBER = "8431224909";

    private EditText etPetType;
    private EditText etFoodType;
    private EditText etQuantity;
    private EditText etAddress;
    private Button btnPlaceOrder;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_food_order);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize views
        etPetType = findViewById(R.id.et_pet_type);
        etFoodType = findViewById(R.id.et_food_type);
        etQuantity = findViewById(R.id.et_quantity);
        etAddress = findViewById(R.id.et_address);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        // Handle place order button click
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void placeOrder() {
        // Get the input values from EditText fields
        String petType = etPetType.getText().toString().trim();
        String foodType = etFoodType.getText().toString().trim();
        int quantity = Integer.parseInt(etQuantity.getText().toString().trim());
        String address = etAddress.getText().toString().trim();

        // Validate the quantity
        if (quantity < 1 || quantity > 5) {
            Toast.makeText(this, "Quantity should be between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new PetFoodOrder object
        PetFoodOrder order = new PetFoodOrder(petType, foodType, quantity, address);

        // Generate a unique key for the order
        String orderId = databaseReference.push().getKey();

        // Save the order to the Firebase Realtime Database
        databaseReference.child(orderId).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Order placed successfully
                        Toast.makeText(PetFoodOrderActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                        // Clear input fields
                        etPetType.setText("");
                        etFoodType.setText("");
                        etQuantity.setText("");
                        etAddress.setText("");

                        // Check for SMS permission
                        if (ContextCompat.checkSelfPermission(PetFoodOrderActivity.this, Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, request it
                            ActivityCompat.requestPermissions(PetFoodOrderActivity.this,
                                    new String[]{Manifest.permission.SEND_SMS},
                                    SMS_PERMISSION_REQUEST_CODE);
                        } else {
                            // Permission is granted, send SMS
                            sendSmsNotification(order);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to place order
                        Toast.makeText(PetFoodOrderActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendSmsNotification(PetFoodOrder order) {
        String smsMessage = "New Pet Food Order:\nPet Type: " + order.getPetType() +
                "\nFood Type: " + order.getFoodType() +
                "\nQuantity: " + order.getQuantity() +
                "\nAddress: " + order.getAddress();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(PHONE_NUMBER, null, smsMessage, null, null);

        Toast.makeText(this, "SMS sent successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                PetFoodOrder order = new PetFoodOrder(etPetType.getText().toString().trim(),
                        etFoodType.getText().toString().trim(),
                        Integer.parseInt(etQuantity.getText().toString().trim()),
                        etAddress.getText().toString().trim());

                sendSmsNotification(order);
            } else {
                // Permission denied, show a toast or handle accordingly
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
