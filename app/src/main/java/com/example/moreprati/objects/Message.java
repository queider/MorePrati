package com.example.moreprati.objects;

public class Message {

    private String messageText;
    private String sender;
    private String reciver;


    // Empty constructor required for Firebase
    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageText, String sender, String reciver) {
        this.messageText = messageText;
        this.sender = sender;
        this.reciver = reciver;
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

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }


}
