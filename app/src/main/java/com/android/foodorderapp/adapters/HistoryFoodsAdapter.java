package com.android.foodorderapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.foodorderapp.R;
import com.android.foodorderapp.model.Menu;

import java.util.List;

public class HistoryFoodsAdapter extends RecyclerView.Adapter<HistoryFoodsAdapter.MenuViewHolder> {

    private List<Menu> menuList;

    public HistoryFoodsAdapter(List<Menu> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_food_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);

        holder.foodNameTV.setText(menu.getName());
        float totalCal = menu.getPrice()*menu.getTotalInCart();
        holder.totalCalTV.setText(String.valueOf(totalCal));
        holder.calTV.setText(String.valueOf(menu.getPrice()));
        holder.qtyTV.setText(String.valueOf(menu.getTotalInCart()));
        // Set other views based on your needs
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTV;
        TextView totalCalTV;
        TextView calTV;
        TextView qtyTV;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTV = itemView.findViewById(R.id.foodNameTV);
            totalCalTV = itemView.findViewById(R.id.totalCalTV);
            calTV = itemView.findViewById(R.id.calTV);
            qtyTV = itemView.findViewById(R.id.qtyTV);
        }
    }
}