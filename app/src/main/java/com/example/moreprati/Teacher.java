package com.example.moreprati;

import java.util.Map;

public class Teacher {
    private String fullname;
    private String mail; // this is an email
    private String city;
    private String uid;

    private Map<String, Boolean> subjects;

    private String wayOfLearning; // ether online, face to face or both

    private int pricePerHour;

    private String description;
    private String image;

    private int rating;

    // Default constructor required for Firebase
    public Teacher() {
    }

    public Teacher(String fullname, String mail, String city, String uid, Map<String, Boolean> subjects,
                   String wayOfLearning, int pricePerHour, String description, String image) {
        this.fullname = fullname;
        this.mail = mail;
        this.city = city;
        this.uid = uid;
        this.subjects = subjects;
        this.wayOfLearning = wayOfLearning;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.image = image;
        this.rating = 0;
    }

    // Getters and setters for all properties

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

    public Map<String, Boolean> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<String, Boolean> subjects) {
        this.subjects = subjects;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
