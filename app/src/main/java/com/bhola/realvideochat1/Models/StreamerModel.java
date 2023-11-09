package com.bhola.realvideochat1.Models;

import com.google.firebase.Timestamp;

public class StreamerModel {
    String  streamerId, callerId;
    Timestamp timestamp;
    int coins;

    public StreamerModel() {
    }

    public StreamerModel(String streamerId, String callerId, Timestamp timestamp, int coins) {
        this.streamerId = streamerId;
        this.callerId = callerId;
        this.timestamp = timestamp;
        this.coins = coins;
    }

    public String getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(String streamerId) {
        this.streamerId = streamerId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
