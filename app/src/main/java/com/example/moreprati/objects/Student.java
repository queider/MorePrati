package com.example.moreprati.objects;

public class Student {

    private String fullname;
    private String mail;
    private String city;
    private String uid;
    private String image;

    private String fcmToken;

    public Student() { // Default constructor required for DataSnapshot.getValue(User.class)

    }
    public Student (String fullname, String mail, String city, String uid, String fcmToken, String image) {
        this.city = city;
        this.fullname = fullname;
        this.mail = mail;
        this.uid = uid;
        this.image = image;
        this.fcmToken = fcmToken;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
