package com.prometrx.whatsappclonefirebase.Model;

public class Users {

    private String userid,imageUrl,username;

    public Users() {

    }

    public Users(String userid, String imageUrl, String username) {
        this.userid = userid;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
