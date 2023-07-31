package com.android.foodorderapp.model;

public class User {
    private Integer age;
    private Double bmr;
    private String gender;
    private Double height;
    private Double weight;
    private String name;



    private String userId;

    public User() {

    }
    public User(Integer age, Double bmr, String gender, Double height, Double weight, String name, String userId) {
        this.age = age;
        this.bmr = bmr;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.name = name;
        this.userId = userId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getBmr() {
        return bmr;
    }

    public void setBmr(Double bmr) {
        this.bmr = bmr;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
