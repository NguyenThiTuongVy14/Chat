package com.example.chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String numberPhone;
    private String name;
    private String avataImage;
    private int loai;
    private Message newMess;
    private String id;
    private String fmc_token;

    // Constructor
    public User() {}

    // Constructor to create from Parcel
    protected User(Parcel in) {
        numberPhone = in.readString();
        name = in.readString();
        avataImage = in.readString();
        loai = in.readInt();
        newMess = in.readParcelable(Message.class.getClassLoader());  // Assuming Message implements Parcelable
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numberPhone);
        dest.writeString(name);
        dest.writeString(avataImage);
        dest.writeInt(loai);
        dest.writeParcelable(newMess, flags);  // Assuming Message implements Parcelable
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // CREATOR object to generate instances of the Parcelable class from a Parcel
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters and Setters
    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvataImage() {
        return avataImage;
    }

    public void setAvataImage(String avataImage) {
        this.avataImage = avataImage;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }

    public Message getNewMess() {
        return newMess;
    }

    public void setNewMess(Message newMess) {
        this.newMess = newMess;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFmc_token() {
        return fmc_token;
    }

    public void setFmc_token(String fmc_token) {
        this.fmc_token = fmc_token;
    }

    @Override
    public String toString() {
        return numberPhone;
    }
}
