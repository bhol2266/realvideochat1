
package com.bhola.livevideochat;

public class Message_Modelclass {
    String message;
    long timeStamp;
    int viewType;//viewType 1 is sender 2 is receiver

    public Message_Modelclass() {
    }

    public Message_Modelclass(String message, long timeStamp, int viewType) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.viewType = viewType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
