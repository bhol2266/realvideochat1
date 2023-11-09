package com.bhola.realvideochat1.Models;

import com.google.firebase.Timestamp;

import java.util.List;

public class CallroomModel {
    String callroomId;
    List<String> userIds;
    Timestamp lastCallTimestamp;
    String lastcallType;
    String lastCalleeId;
    int lastcallDuration;

    public CallroomModel() {
    }

    public CallroomModel(String callroomId, List<String> userIds, Timestamp lastCallTimestamp, String lastcallType, String lastCalleeId, int lastcallDuration) {
        this.callroomId = callroomId;
        this.userIds = userIds;
        this.lastCallTimestamp = lastCallTimestamp;
        this.lastcallType = lastcallType;
        this.lastCalleeId = lastCalleeId;
        this.lastcallDuration = lastcallDuration;
    }

    public String getCallroomId() {
        return callroomId;
    }

    public void setCallroomId(String callroomId) {
        this.callroomId = callroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastCallTimestamp() {
        return lastCallTimestamp;
    }

    public void setLastCallTimestamp(Timestamp lastCallTimestamp) {
        this.lastCallTimestamp = lastCallTimestamp;
    }

    public String getLastcallType() {
        return lastcallType;
    }

    public void setLastcallType(String lastcallType) {
        this.lastcallType = lastcallType;
    }

    public String getLastCalleeId() {
        return lastCalleeId;
    }

    public void setLastCalleeId(String lastCalleeId) {
        this.lastCalleeId = lastCalleeId;
    }

    public int getLastcallDuration() {
        return lastcallDuration;
    }

    public void setLastcallDuration(int lastcallDuration) {
        this.lastcallDuration = lastcallDuration;
    }
}