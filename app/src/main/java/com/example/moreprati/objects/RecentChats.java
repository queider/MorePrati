package com.example.moreprati.objects;

import java.util.Calendar;

public class RecentChats {
    private String fullname;
    private String imageUrl;

    private String lastMessage;
    private String chatUserId;
    public RecentChats() {
        // Default constructor required for Firebase
    }

    public RecentChats(String lastMessage, String fullname, String imageUrl, String chatUserId) {
        this.lastMessage = lastMessage;
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.chatUserId = chatUserId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }


}
