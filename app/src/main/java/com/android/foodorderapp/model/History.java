package com.android.foodorderapp.model;

import java.util.List;

public class History {
    List<Menu> menuList;
    Double recommendedCalories, totalCalories;
    Boolean targetAchieved;
    String date;

    public History() {
    }

    public History( List<Menu> menuList, Double recommendedCalories, Double totalCalories, String date) {
        this.menuList = menuList;
        this.recommendedCalories = recommendedCalories;
        this.totalCalories = totalCalories;
        this.targetAchieved = getTargetAchieved();
        this.date = date;
    }


    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList( List<Menu> menuList) {
        this.menuList = menuList;
    }

    public Double getRecommendedCalories() {
        return recommendedCalories;
    }

    public void setRecommendedCalories(Double recommendedCalories) {
        this.recommendedCalories = recommendedCalories;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Boolean getTargetAchieved() {
        return this.totalCalories > this.recommendedCalories;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
