package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class userlogin extends AppCompatActivity {
EditText editTextEmail,editTextPassword;
Button submit,signin;
FirebaseAuth mAuth ;
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent=new Intent(getApplicationContext(),actuallogin.class);
//            startActivity(intent);
//            finish();
//        }
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        mAuth= FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.EMAIL);
        editTextPassword=findViewById(R.id.PASSWORD);
        submit=findViewById(R.id.SUBMIT1);
        ProgressBar progressBar;
        progressBar=findViewById(R.id.progressbar);

        signin=findViewById(R.id.LOGIN);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(userlogin.this,actuallogin.class);
                startActivity(intent);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(userlogin.this,"ENTER THE EMAIL",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(userlogin.this,"ENTER THE password",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(view.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(userlogin.this, "sucessful.", Toast.LENGTH_SHORT ).show();
                                    Intent intent=new Intent(getApplicationContext(),actuallogin.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(userlogin.this, "failed.", Toast.LENGTH_SHORT ).show();

                            }
                        }

                    });


            }
        });

    }
}