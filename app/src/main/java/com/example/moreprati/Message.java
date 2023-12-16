package com.example.moreprati;

public class Message {

    private String messageText;
    private String sender;


    // Empty constructor required for Firebase
    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageText, String sender) {
        this.messageText = messageText;
        this.sender = sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSender() {
        return sender;
    }

    public void setSedner(String sedner) {
        this.sender = sender;
    }


}
