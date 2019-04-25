package com.example.ezserve;

public class BillHistory {
    private String date, restaurant;
    private float total;

    public BillHistory() {

    }

    public BillHistory(String date, String restaurant, float total) {
        this.date = date;
        this.restaurant = restaurant;
        this.total = total;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
