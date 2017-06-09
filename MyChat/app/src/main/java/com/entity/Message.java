package com.entity;

/**
 * Created by Logan on 2016/10/20.
 */

public class Message {
    public static final int TEXT = 1, VOICE = 2;
    private String sender;
    private String receiver;
    private String date;
    private String text;
    private Type type;
    private int messageType = TEXT;

    public enum Type {
        MESSAGE_FROM, MESSAGE_TO
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
