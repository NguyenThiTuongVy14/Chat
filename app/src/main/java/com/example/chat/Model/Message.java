package com.example.chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Message implements Parcelable {
    private String id_Sender;
    private String id_Receive;
    private String message;
    private Timestamp timestamp;
    private boolean isMessLocation;
    private double latitude;  // Vị trí latitude
    private double longitude; // Vị trí longitude

    // Constructor mặc định
    public Message() {
    }

    // Constructor với thông tin đầy đủ
    public Message(String id_Sender, String id_Receive, String message, Timestamp timestamp,
                   boolean isMessLocation, double latitude, double longitude) {
        this.id_Sender = id_Sender;
        this.id_Receive = id_Receive;
        this.message = message;
        this.timestamp = timestamp;
        this.isMessLocation = isMessLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor để tạo từ Parcel
    protected Message(Parcel in) {
        id_Sender = in.readString();
        id_Receive = in.readString();
        message = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader()); // Đọc Timestamp từ Parcel
        isMessLocation = in.readByte() != 0; // Đọc boolean từ Parcel
        latitude = in.readDouble();  // Đọc latitude
        longitude = in.readDouble(); // Đọc longitude
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_Sender);
        dest.writeString(id_Receive);
        dest.writeString(message);
        dest.writeParcelable(timestamp, flags); // Ghi Timestamp vào Parcel
        dest.writeByte((byte) (isMessLocation ? 1 : 0));  // Ghi boolean vào Parcel
        dest.writeDouble(latitude);  // Ghi latitude vào Parcel
        dest.writeDouble(longitude); // Ghi longitude vào Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // CREATOR object để tạo đối tượng từ Parcel
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

    // Getters và Setters
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

    public boolean isMessLocation() {
        return isMessLocation;
    }

    public void setMessLocation(boolean messLocation) {
        isMessLocation = messLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
