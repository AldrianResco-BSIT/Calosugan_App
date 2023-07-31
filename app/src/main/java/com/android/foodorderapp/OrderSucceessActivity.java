package com.android.foodorderapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.foodorderapp.adapters.PlaceYourOrderAdapter;
import com.android.foodorderapp.model.History;
import com.android.foodorderapp.model.Menu;
import com.android.foodorderapp.model.RestaurantModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderSucceessActivity extends AppCompatActivity {


    private RecyclerView cartItemsRecyclerView;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    private Button createPdfButton;
    float recommendedCalories, totalCalories;

    private final static int REQUEST_CODE = 1232;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_succeess);

       // createPdfButton = findViewById(R.id.createPdfButton);
        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        recommendedCalories =  getIntent().getFloatExtra("recommendedCalories",0);
        totalCalories =  getIntent().getFloatExtra("totalCalories",0);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(restaurantModel.getName());
            actionBar.setSubtitle(restaurantModel.getAddress());
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        Button buttonDone = findViewById(R.id.buttonDone);



        buttonDone.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSucceessActivity.this, HomePage.class);
            startActivity(intent);
            finish();
                }
        );
        TextView result = findViewById(R.id.result);
        result.setText("Target Calories:"+recommendedCalories +"\nTotal Calories intake: "+totalCalories );
        saveOrderToHistory(restaurantModel);
        initRecyclerView(restaurantModel);

//        createPdfButton.setOnClickListener(v -> {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                createAndSavePdf();
//            } else {
//                askPermissions();
//            }
//        });
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }
    private void saveOrderToHistory(RestaurantModel restaurantModel) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();


        for (Menu menu : restaurantModel.getMenus()) {
            if (menu.getTotalInCart() > 0) {
                String line = restaurantModel.getName() + ","
                        + menu.getName() + ","
                        + menu.getTotalInCart() + ","
                        + menu.getPrice() * menu.getTotalInCart() + "\n";
            }
        }

        //GET RECOMMENDED CAL
        SharedPreferences prefs = getSharedPreferences("PREFS_PROFILE", MODE_PRIVATE);
        String recommendedCal = prefs.getString("bmr", null);

        float subTotalAmount = 0f;

        for(Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        long dateTimestamp = System.currentTimeMillis();
        String dateTime = DateUtils.convertTimestampToDateTime(dateTimestamp);

        ProgressDialog loading = new ProgressDialog(OrderSucceessActivity.this);
        loading.setCancelable(false);
        loading.setMessage("Saving...");
        loading.show();

        //  CREATE HISTORY
        History history = new History();
        history.setDate(DateUtils.formatDateTimeToWordDate(dateTime));
        history.setMenuList(restaurantModel.getMenus());
        history.setRecommendedCalories(Double.parseDouble(recommendedCal));
        history.setTotalCalories(Double.parseDouble(String.valueOf(subTotalAmount) ));


        mDatabase.child("history").child(userId).child(String.valueOf(System.currentTimeMillis())).setValue(history)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loading.dismiss();
                            Toast.makeText(OrderSucceessActivity.this, "Data Saved", Toast.LENGTH_LONG).show();
                        } else {
                            // An error occurred while writing to the database
                            // You can handle the error here
                            Exception e = task.getException();
                            loading.dismiss();
                            Toast.makeText(OrderSucceessActivity.this, "An error occured while saving data", Toast.LENGTH_LONG).show();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


    }

    private void createAndSavePdf() {
        String pdfFileName = "OrderSummary.pdf";
        File pdfFile = new File(Environment.getExternalStorageDirectory(), pdfFileName);

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add content to the PDF document
            document.add(new Paragraph("Order Summary"));

            // Add an image (optional)
            Image img = new Image(ImageDataFactory.create(String.valueOf(getResources().getDrawable(R.drawable.your_image_drawable))));
            document.add(img);

            // Add your content here, for example, you can loop through the cart items and add them as paragraphs.

            // Close the document
            document.close();

            Toast.makeText(this, "PDF created and saved successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
