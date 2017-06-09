package com.entity;

/**
 * Created by Logan on 2017/1/17.
 */

public class User {
    private int id=0;
    private String username = "";
    private String password = "";
    private String name = "";
    private String image = "";
    private String phone = "";
    private String friends="";

    public User() {}

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getFriends() {
        return friends;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
