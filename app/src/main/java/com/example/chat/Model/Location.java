package com.example.chat.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location implements Serializable {
    private double locationX;
    private double locationY;
    private String imgMarker;

    public Location() {
    }

    // Constructor
    public Location(double locationX, double locationY, String imgMarker) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.imgMarker = imgMarker;
    }



    public double getLocationX() {
        return locationX;
    }

    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    public String getImgMarker() {
        return imgMarker;
    }

    public void setImgMarker(String imgMarker) {
        this.imgMarker = imgMarker;
    }
}
