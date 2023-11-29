package com.example.moreprati;

public class Message {
    private String senderId;
    private String messageText;
    private long timestamp; // You can add a timestamp to each message

    // Empty constructor required for Firebase
    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String senderId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
