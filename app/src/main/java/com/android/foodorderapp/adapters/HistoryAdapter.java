package com.android.foodorderapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.foodorderapp.DateUtils;
import com.android.foodorderapp.R;
import com.android.foodorderapp.model.History;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<History> historyList;

    private AdapterView.OnItemClickListener itemClickListener;

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);

        if (history.getTargetAchieved()) {
            holder.achievedTV.setText("Goal Achieved");
        } else {
            holder.achievedTV.setText("Failed");
        }

        // Format the total calories with commas for every thousand
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedTotalCalories = numberFormat.format(history.getTotalCalories());
        holder.totalCaloriesTextView.setText(formattedTotalCalories);

        // Format the recommended calories with commas for every thousand
        String formattedRecommendedCalories = numberFormat.format(history.getRecommendedCalories());
        holder.recommendedCaloriesTextView.setText(formattedRecommendedCalories);

        holder.dateTextView.setText(history.getDate());

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(null, view, position, holder.getItemId());
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistoryList(List<History> filteredHistoryList) {
        List<History> filteredList = null;
        this.historyList = filteredList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView totalCaloriesTextView, recommendedCaloriesTextView, dateTextView,achievedTV;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            totalCaloriesTextView = itemView.findViewById(R.id.totalCaloriesTV);
            recommendedCaloriesTextView = itemView.findViewById(R.id.recommendedCaloriesTV);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            achievedTV = itemView.findViewById(R.id.achievedTV);
        }
    }
    // Define an interface for the item click listener
    public interface OnItemClickListener {
        void onItemClick(History history);
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    
}