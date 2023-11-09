package com.bhola.realvideochat1.Models;

import com.google.firebase.Timestamp;

public class CallogModel {

    String calleeId, callType;
    Timestamp callTimestamp;
    boolean call_connected;
    int callDurationSeconds;

    public CallogModel() {
    }

    public CallogModel(String calleeId, String callType, Timestamp callTimestamp, boolean call_connected, int callDurationSeconds) {
        this.calleeId = calleeId;
        this.callType = callType;
        this.callTimestamp = callTimestamp;
        this.call_connected = call_connected;
        this.callDurationSeconds = callDurationSeconds;
    }

    public String getCalleeId() {
        return calleeId;
    }

    public void setCalleeId(String calleeId) {
        this.calleeId = calleeId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public Timestamp getCallTimestamp() {
        return callTimestamp;
    }

    public void setCallTimestamp(Timestamp callTimestamp) {
        this.callTimestamp = callTimestamp;
    }

    public boolean isCall_connected() {
        return call_connected;
    }

    public void setCall_connected(boolean call_connected) {
        this.call_connected = call_connected;
    }

    public int getCallDurationSeconds() {
        return callDurationSeconds;
    }

    public void setCallDurationSeconds(int callDurationSeconds) {
        this.callDurationSeconds = callDurationSeconds;
    }
}
