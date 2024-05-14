package com.example.moreprati.objects;

public class Student {

    private String fullname;
    private String email;
    private String city;
    private String uid;
    private String imageUrl;

    private String fcmToken;


    public Student() { // Default constructor required for DataSnapshot.getValue(User.class)

    }
    public Student (String fullname, String email, String city, String uid, String fcmToken, String imageUrl) {
        this.city = city;
        this.fullname = fullname;
        this.email = email;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.fcmToken = fcmToken;

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
