package com.example.moreprati.objects;

public class Student {

    private String fullname;
    private String mail;
    private String city;
    private String uid;

    private String fcmToken;

    // Default constructor required for DataSnapshot.getValue(User.class)
    public Student() {}
    public Student (String fullname, String mail, String city, String uid, String fcmToken) {
        this.city = city;
        this.fullname = fullname;
        this.mail = mail;
        this.uid = uid;
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
}
