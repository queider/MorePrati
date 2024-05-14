package com.example.moreprati.objects;

import java.io.Serializable;

public class RecentChat implements Serializable {
    private String fullname;
    private String imageUrl;

    private String chatUserId;

    private String chatName;
    private String fcmToken;

    private String lastMessage;
    public RecentChat(String fcmToken) {
        // Default constructor required for Firebase
    }

    public RecentChat(String fullname, String imageUrl, String chatUserId, String chatName, String fcmToken, String lastMessage) {
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.chatUserId = chatUserId;
        this.chatName = chatName;
        this.fcmToken = fcmToken;
        this.lastMessage = lastMessage;
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


    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
