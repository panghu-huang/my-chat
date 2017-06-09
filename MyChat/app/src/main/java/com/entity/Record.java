package com.entity;

/**
 * Created by Logan on 2017/2/5.
 */

public class Record {
    private int id = 0;
    private String username = "";
    private String name = "";
    private String img = "";
    private String targetName = "";
    private String targetUsername = "";
    private String targetImg = "";
    private String desc = "";
    private String result = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getTargetImg() {
        return targetImg;
    }

    public void setTargetImg(String targetImg) {
        this.targetImg = targetImg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "desc='" + desc + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", targetName='" + targetName + '\'' +
                ", targetUsername='" + targetUsername + '\'' +
                ", targetImg='" + targetImg + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
