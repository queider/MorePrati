package com.example.moreprati;

import java.util.List;

public class Teacher {
    private String fullname;
    private String mail; // this is an email
    private String city;
    private String uid;

    private List<String> subjects; // can be math, english, guitar and music

    private String wayOfLearning; //ether online, face to face or both

    private int pricePerHour;

    private String description;
    private String  profilePic; // represented a String of 64bit incoding

    private int rating;

    public Teacher(String fullname, String mail, String city, String uid, List<String> subjects,
                   String wayOfLearning, int pricePerHour, String description,  String profilePic) {
        this.fullname = fullname;
        this.mail = mail;
        this.city = city;
        this.uid = uid;
        this.subjects = subjects;
        this.wayOfLearning = wayOfLearning;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.profilePic =  profilePic;
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

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String setProfilePic) {
        this.profilePic = profilePic;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.pricePerHour = rating;
    }

}
