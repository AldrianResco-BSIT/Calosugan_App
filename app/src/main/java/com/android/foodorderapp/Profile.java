package com.android.foodorderapp;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.foodorderapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    private EditText etAge, etHeight, etWeight,etName;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnCalculate, btnUpdateFirebase;
    private TextView tvResult;
    private String userId;


    private Integer age;
    private String name, gender;
    private Double bmr, weight, height;

    // Firebase
    private DatabaseReference mDatabase;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupViews();

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //GET AUTH INSTANCE TO GET USERID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null) {
             userId = firebaseUser.getUid();
        }

        fetchProfileData();

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalories();
            }
        });

        btnUpdateFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFirebase();
            }
        });
    }

    private void fetchProfileData() {
        ProgressDialog progressDialog = new ProgressDialog(Profile.this);
        progressDialog.setMessage("Fetching Profile Data...");
        progressDialog.setCancelable(false); // Prevent the user from dismissing it with the back button

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        progressDialog.show();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("dataSnapshot", "data fetched: ");
                    String fetchedName, fetchedGender;
                    Integer fetchedAge;
                    Double fetchedWeight, fetchedHeight, fetchedBmr;
                    fetchedAge = dataSnapshot.child("age").getValue(Integer.class);
                    fetchedName = dataSnapshot.child("name").getValue(String.class);

                    fetchedGender = dataSnapshot.child("gender").getValue(String.class);
                    fetchedWeight = dataSnapshot.child("weight").getValue(Double.class);
                    fetchedHeight = dataSnapshot.child("height").getValue(Double.class);
                    fetchedBmr = dataSnapshot.child("bmr").getValue(Double.class);

                    //SHARE PREF
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS_PROFILE", MODE_PRIVATE).edit();
                    editor.putString("bmr", String.valueOf(fetchedBmr));
                    editor.apply();

                    if(fetchedAge!=null){
                        etAge.setText(String.valueOf(fetchedAge));
                    }
                    if(fetchedName!=null){
                        etName.setText(fetchedName);
                    }
                    if(fetchedGender!=null){
                        if (fetchedGender.equals("male")){
                            rgGender.check(R.id.rb_male);
                        }else{
                            rgGender.check(R.id.rb_female);
                        }
                    }
                    if(fetchedWeight!=null){
                        etWeight.setText(String.valueOf(fetchedWeight));
                    }
                    if(fetchedHeight!=null){
                        etHeight.setText(String.valueOf(fetchedHeight));
                    }
                    if(fetchedBmr!=null){
                        tvResult.setText("Recommended daily calorie intake is: "+fetchedBmr);
                    }

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(Profile.this, "An error occured while fetching data", Toast.LENGTH_LONG).show();
                Log.d("FETCH PROFILE DATA", "onCancelled: "+databaseError);
            }
        });
    }


    public void calculateCalories() {
        // Get user inputs
        age = Integer.parseInt(etAge.getText().toString());
        height = Double.parseDouble(etHeight.getText().toString());
        weight = Double.parseDouble(etWeight.getText().toString());
        int genderId = rgGender.getCheckedRadioButtonId();

        //validate input

        // Calculate BMR based on gender
        double rawBmr = 0.0;
        if (genderId == rbMale.getId()) {
            rawBmr = 66 + (6.23 * weight) + (12.7 * height) - (6.8 * age);
        } else {
            rawBmr = 655 + (4.35 * weight) + (4.7 * height) - (4.7 * age);
        }
        //truncate bmr
        bmr = Math.floor(rawBmr * 100) / 100;

        // Format the BMR with commas for every thousand
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedBmr = numberFormat.format(bmr);

        // Display the result
        tvResult.setText("Recommended daily calorie intake is: " + formattedBmr);
    }

    public void updateFirebase() {
        // Create a User object with the entered data
        name = etName.getText().toString();
        int genderId = rgGender.getCheckedRadioButtonId();
        if(genderId == R.id.rb_male){
            gender = "male";
        }else{
            gender = "female";
        }
        age = Integer.parseInt(etAge.getText().toString()) ;


        User user = new User();

        user.setAge(age);
        user.setName(name);
        user.setHeight(height);
        user.setWeight(weight);
        user.setGender(gender);
        user.setBmr(bmr);
        user.setUserId(userId);
        // Write the user object to Firebase
        ProgressDialog loading = new ProgressDialog(Profile.this);
        loading.setCancelable(false);
        loading.setMessage("Updating...");
        loading.show();

        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loading.dismiss();
                            Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                            fetchProfileData();
                        } else {
                            // An error occurred while writing to the database
                            // You can handle the error here
                            Exception e = task.getException();
                            loading.dismiss();
                            Toast.makeText(Profile.this, "An error occured while updating", Toast.LENGTH_LONG).show();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }
    private void setupViews() {
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        btnCalculate = findViewById(R.id.btn_calculate);
        btnUpdateFirebase = findViewById(R.id.btn_update_firebase);
        tvResult = findViewById(R.id.tv_result);
    }

}