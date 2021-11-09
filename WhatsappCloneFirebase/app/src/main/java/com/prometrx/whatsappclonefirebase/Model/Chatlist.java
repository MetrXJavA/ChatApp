package com.prometrx.whatsappclonefirebase.Model;

public class Chatlist {


    private String recv,send,imageUrl,userName;



    public Chatlist(String recv, String send, String imageUrl, String userName) {
        this.recv = recv;
        this.send = send;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }

    public Chatlist() {

    }



    public String getRecv() {
        return recv;
    }

    public void setRecv(String recv) {
        this.recv = recv;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
