package com.bhola.realvideochat1;

public class UserBotMsg {
    private String msg;
    private String mimeType;
    private String extraMsg;
    private String dateTime;
    private int nextMsgDelay;
    private int read;
    private int sent;

    public UserBotMsg() {
    }

    public UserBotMsg(String msg, String mimeType, String extraMsg, String dateTime, int nextMsgDelay, int read, int sent) {
        this.msg = msg;
        this.mimeType = mimeType;
        this.extraMsg = extraMsg;
        this.dateTime = dateTime;
        this.nextMsgDelay = nextMsgDelay;
        this.read = read;
        this.sent = sent;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getNextMsgDelay() {
        return nextMsgDelay;
    }

    public void setNextMsgDelay(int nextMsgDelay) {
        this.nextMsgDelay = nextMsgDelay;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }
}
