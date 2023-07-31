package com.android.foodorderapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.foodorderapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(SignUp.this, Login.class));
        });
    }

    private void createUser(){
        ProgressDialog dialog;
        dialog = new ProgressDialog(SignUp.this, android.R.style.Theme_DeviceDefault_Dialog);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");


        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }else{
            dialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //GET FIREBASE AUTH INSTANCE
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        //GET UUID FROM AUTH
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            //CREATE USER INSTANCE
                            User newUser = new User();
                            newUser.setUserId(userId);

                            //DATABASE REF
                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference();

                            //ADD USER DETAILS TO REALTIME DATABASE
                            mDatabase.child("users").child(userId).setValue(newUser);

                            dialog.dismiss();
                            //CHECK FIRST IF RECOMMENDED CAL IS CALCULATED ALREADY

                            Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, Login.class));
                        }
                    }else{
                        dialog.dismiss();
                        Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}

