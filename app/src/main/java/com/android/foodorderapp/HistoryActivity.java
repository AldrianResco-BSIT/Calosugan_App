package com.android.foodorderapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.foodorderapp.adapters.HistoryAdapter;
import com.android.foodorderapp.adapters.HistoryFoodsAdapter;
import com.android.foodorderapp.model.History;
import com.android.foodorderapp.model.Menu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("History");

        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyList);
        recyclerView.setAdapter(historyAdapter);

        historyAdapter.setOnItemClickListener(this);
        loadHistory();
    }

    private void loadHistory() {
        ProgressDialog loading = new ProgressDialog(HistoryActivity.this);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("history");

        Query query = historyRef.child(userId).limitToLast(100);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                historyList.clear();
                for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                    History history = historySnapshot.getValue(History.class);
                    // Add the history to the beginning of the list
                    historyList.add(0,history);
                }
                historyAdapter.notifyDataSetChanged();
                loading.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.dismiss();
            }
        });
    }
    // Implement the onItemClick method of the AdapterView.OnItemClickListener interface
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        History history = historyList.get(position);
        // Show a dialog with history details
        showDialogWithHistory(history);
    }
    private void showDialogWithHistory(History history) {
        // Create and show a dialog with the history details


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

        // Use HTML formatting to set the title color

        String dateTime = history.getDate();


            //        for receive historys
            // Inflate the custom layout for the AlertDialog
            View customLayout = getLayoutInflater().inflate(R.layout.custom_history_dialog, null);

        RecyclerView historyFoodRecyclerView = customLayout.findViewById(R.id.historyFoodRecyclerView);
        HistoryFoodsAdapter foodsAdapter = new HistoryFoodsAdapter(history.getMenuList());
        historyFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyFoodRecyclerView.setAdapter(foodsAdapter);

            // Find the UI elements in the custom layout and set their text with history details
            TextView dateTimeTextView = customLayout.findViewById(R.id.dateTimeTV);
            dateTimeTextView.setText(dateTime);

            TextView recommendedCaloriesTV = customLayout.findViewById(R.id.recommendedCaloriesTV);
             recommendedCaloriesTV.setText(String.valueOf(history.getRecommendedCalories()));


            TextView statusTV = customLayout.findViewById(R.id.statusTV);
            if(history.getTargetAchieved()){
                statusTV.setText("Goal Achieved");
            }else{
                statusTV.setText("Failed");
            }

            TextView totalCaloriesTV = customLayout.findViewById(R.id.totalCaloriesTV);
              totalCaloriesTV.setText(String.valueOf(history.getTotalCalories()));

            builder.setView(customLayout)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();



    }

    // Method to handle the date selection button click
    public void onSelectDateButtonClick(View view) {
        // Show a date picker dialog to let the user choose the target date
        showDatePickerDialog();
    }

    // Method to show the date picker dialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Convert the selected date to a string representation (e.g., "YYYY-MM-DD")
                        String selectedDate = DateUtils.formatDate(year, month, dayOfMonth);

                        // Call the filterHistoryByDate method with the selected date
                        filterHistoryByDate(selectedDate);
                    }
                },
                // Set the default date to the current date
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Method to filter the history items by date
    private void filterHistoryByDate(String targetDate) {
        // Create a new list to store the filtered history items
        List<History> filteredHistoryList = new ArrayList<>();

        // Iterate through the original historyList to find items with the matching date
        for (History history : historyList) {
            if (DateUtils.isSameDate(history.getDate(), targetDate)) {
                filteredHistoryList.add(history);
            }
        }

        // Update the historyAdapter with the filtered list
        historyAdapter.updateHistoryList(filteredHistoryList);
    }

}
