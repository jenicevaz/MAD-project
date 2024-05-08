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

public class admin_volunteer extends AppCompatActivity {
    private TextView userDetailsTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_volunteer);
        userDetailsTextView = findViewById(R.id.userDetailsTextView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    stringBuilder.append("Name: ").append(user.getName()).append("\n");
                    stringBuilder.append("Address: ").append(user.getAddress()).append("\n");
                    stringBuilder.append("Email: ").append(user.getEmail()).append("\n");
                    stringBuilder.append("Phone: ").append(user.getPhone()).append("\n\n");
                }
                userDetailsTextView.setText(stringBuilder.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

    }
}
