package com.android.foodorderapp;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {

    Button foodBtn, profileBtn, aboutusBtn, btnLogOut ,btnHistory;

    FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("CaloSugan App");
        fetchProfileData();

        foodBtn = findViewById(R.id.foodBtn);
      //  foodcalculatorBtn = findViewById(R.id.foodcalculatorBtn);
        profileBtn = findViewById(R.id.profileBtn);
        aboutusBtn = findViewById(R.id.aboutusBtn);
        btnLogOut = findViewById(R.id.btnLogout);
        btnHistory = findViewById(R.id.btnHistory);

        mAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(HomePage.this, Login.class));
        });


        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CHECK FIRST IF RECOMMENDED CAL IS CALCULATED ALREADY
                SharedPreferences prefs = getSharedPreferences("PREFS_PROFILE", MODE_PRIVATE);
                String recommendedCal = prefs.getString("bmr", null);
                Log.d("recommendedCal", recommendedCal);

                if(recommendedCal != null && !recommendedCal.equals("null")){
                    Intent intent = new Intent(HomePage.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                    builder.setTitle("No Recommended Calorie data");
                    builder.setMessage("Please complete your profile first and save it to use the this feature.");
                    builder.setPositiveButton("Go to Profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when the "OK" button is clicked.
                            dialog.dismiss();
                            Intent intent = new Intent(HomePage.this, Profile.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when the "Cancel" button is clicked.
                            dialog.dismiss();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
            }
        });
        aboutusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, AboutUs.class);
                startActivity(intent);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

    }
    private void fetchProfileData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = "";
        if(firebaseUser!=null) {
            userId = firebaseUser.getUid();
        }
        ProgressDialog progressDialog = new ProgressDialog(HomePage.this);
        progressDialog.setMessage("Fetching Profile Data...");
        progressDialog.setCancelable(false); // Prevent the user from dismissing it with the back button

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        progressDialog.show();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("dataSnapshot", "data fetched: ");
                    Double fetchedBmr;
                    fetchedBmr = dataSnapshot.child("bmr").getValue(Double.class);
                    Log.d("fetchedBmr", "onDataChange: "+fetchedBmr);
                    //SHARE PREF
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS_PROFILE", MODE_PRIVATE).edit();
                    editor.putString("bmr", String.valueOf(fetchedBmr));
                    editor.apply();

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(HomePage.this, "An error occured while fetching data", Toast.LENGTH_LONG).show();
                Log.d("FETCH PROFILE DATA", "onCancelled: "+databaseError);
            }
        });
    }

}