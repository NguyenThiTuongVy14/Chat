package com.example.chat.Model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Message {
    private String id_Sender;
    private String id_Receive;
    private String message;
    private Timestamp timestamp;

    public Message() {
    }

    public String getId_Sender() {
        return id_Sender;
    }

    public void setId_Sender(String id_Sender) {
        this.id_Sender = id_Sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_Receive() {
        return id_Receive;
    }

    public void setId_Receive(String id_Receive) {
        this.id_Receive = id_Receive;
    }
}
