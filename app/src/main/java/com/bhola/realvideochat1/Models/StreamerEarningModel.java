package com.bhola.realvideochat1.Models;

public class StreamerEarningModel {
    String date;
    int coins;

    public StreamerEarningModel() {
    }

    public StreamerEarningModel(String date, int coins) {
        this.date = date;
        this.coins = coins;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
