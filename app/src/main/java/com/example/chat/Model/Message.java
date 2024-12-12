package com.example.chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Message implements Parcelable {
    private String id_Sender;
    private String id_Receive;
    private String message;
    private Timestamp timestamp;

    // Constructor
    public Message() {
    }

    // Constructor to create from Parcel
    protected Message(Parcel in) {
        id_Sender = in.readString();
        id_Receive = in.readString();
        message = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader()); // Read Timestamp from Parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_Sender);
        dest.writeString(id_Receive);
        dest.writeString(message);
        dest.writeParcelable(timestamp, flags);  // Write Timestamp to Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // CREATOR object to generate instances of the Parcelable class from a Parcel
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    // Getters and Setters
    public String getId_Sender() {
        return id_Sender;
    }

    public void setId_Sender(String id_Sender) {
        this.id_Sender = id_Sender;
    }

    public String getId_Receive() {
        return id_Receive;
    }

    public void setId_Receive(String id_Receive) {
        this.id_Receive = id_Receive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
