package com.example.ezserve;

public class BillHistory {
    private String date, restaurant, total;

    public BillHistory() {


    }

    public BillHistory(String date, String restaurant, String total) {
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
