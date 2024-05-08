package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class work extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Button button1=findViewById(R.id.volunteer);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(work.this,form.class);
                startActivity(intent);
            }
        });
        Button button2=findViewById(R.id.lost);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(work.this,lost.class);
                startActivity(intent);
            }
        });
        Button button3=findViewById(R.id.help);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(work.this,help.class);
                startActivity(intent);
            }
        });
        Button button4=findViewById(R.id.message);
        button4.setOnClickListener(view -> {
            Intent intent=new Intent(work.this,message.class);
            startActivity(intent);
        });
        Button button5=findViewById(R.id.order);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(work.this,PetFoodOrderActivity.class);
                startActivity(intent);
            }
        });
    }
}