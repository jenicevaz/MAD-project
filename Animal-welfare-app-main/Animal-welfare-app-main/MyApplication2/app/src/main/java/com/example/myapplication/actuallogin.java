package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class actuallogin extends AppCompatActivity {
    EditText editTextEmail1,editTextPassword1;
    Button submit1;
   FirebaseAuth mAuth ;
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent=new Intent(getApplicationContext(),actuallogin.class);
//        startActivity(intent);
//        finish();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuallogin);
        mAuth= FirebaseAuth.getInstance();
        editTextEmail1=findViewById(R.id.EMAIL);
        editTextPassword1=findViewById(R.id.PASSWORD1);
        submit1=findViewById(R.id.SUBMIT1);
        ProgressBar progressBar;
        progressBar=findViewById(R.id.progressbar);

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(editTextEmail1.getText());
                password=String.valueOf(editTextPassword1.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(actuallogin.this,"ENTER THE EMAIL",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(actuallogin.this,"ENTER THE password",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"login sucessfull",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getApplicationContext(),work.class);
                                    startActivity(intent);
                                    finish();
                                    // Sign in success, update UI with the signed-in user's information
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(actuallogin.this,"login failed.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            });
        }

    }
