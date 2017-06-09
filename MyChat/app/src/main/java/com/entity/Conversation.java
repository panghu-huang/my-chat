package com.entity;


/**
 * Created by Logan on 2017/2/11.
 */

public class Conversation {
    private String sender = "";
    private String senderName = "";
    private String receiver = "";
    private int unReadCount = 0;
    private String time = "";
    private String lastMessage = "";
    private int type = Information.PRIVATE;
    private String senderImg = "img/ic_logo.png";

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                ", sender='" + sender + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiver='" + receiver + '\'' +
                ", unReadCount='" + unReadCount + '\'' + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", type=" + type +
                '}';
    }
}
