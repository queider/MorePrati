package com.example.moreprati.objects;

import java.util.Map;

public class Teacher {
    private String fullname;
    private String email; // this is an email
    private String city;
    private String uid;

    private Map<String, Boolean> subjects;
    private Map<String, Boolean> citySubjects;

    private String wayOfLearning; // ether online, face to face or both

    private int pricePerHour;

    private String description;
    private String imageUrl;

    private float rating;
    private int howManyRated;

    private String fcmToken;


    public Teacher() {
        // Default constructor required for Firebase

    }

    public Teacher(String fullname, String email, String city, String uid, Map<String, Boolean> subjects, Map<String, Boolean> citySubjects,
                   String wayOfLearning, int pricePerHour, String description, String imageUrl,String fcmToken) {
        this.fullname = fullname;
        this.email = email;
        this.city = city;
        this.uid = uid;
        this.subjects = subjects;
        this.citySubjects = citySubjects;
        this.wayOfLearning = wayOfLearning;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = 0;
        this.howManyRated = 0;
        this.fcmToken = fcmToken;
    }

    // Getters and setters for all properties

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
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

    public Map<String, Boolean> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<String, Boolean> subjects) {
        this.subjects = subjects;
    }


    public Map<String, Boolean> getCitySubjects() {
        return citySubjects;
    }

    public void setCitySubjects(Map<String, Boolean> citySubjects) {
        this.citySubjects = citySubjects;
    }



    public String getWayOfLearning() {
        return wayOfLearning;
    }

    public void setWayOfLearning(String wayOfLearning) {
        this.wayOfLearning = wayOfLearning;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public int  getHowManyRated() {
        return howManyRated;
    }
    public void setHowManyRated(int howManyRated) {
        this.howManyRated = howManyRated;
    }



}
