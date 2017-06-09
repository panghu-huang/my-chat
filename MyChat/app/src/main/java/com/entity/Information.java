package com.entity;

/**
 * Created by Logan on 2017/1/17.
 */

public class Information {
    public static final int SYSTEM = 1, PRIVATE = 2, GROUP = 3, FRIEND = 4, DONGTAI = 5, PERSONAL = 6;
    private int type = PRIVATE;
    private String message = "";
    private String sender = "";
    private String receiver = "";
    private String extra = "";
    private String senderName = "";
    private String time = "";
    private String senderImg;

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Information() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Information{" +
                "extra='" + extra + '\'' +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderName='" + senderName + '\'' +
                ", time='" + time + '\'' +
                ", senderImg='" + senderImg + '\'' +
                '}';
    }
}
