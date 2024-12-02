package com.example.chat.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    private String numberPhone;
    private String name;
    private String avataImage;
    private int loai;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getAvataImage() {
        return avataImage;
    }

    public void setAvataImage(String avataImage) {
        this.avataImage = avataImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "numberPhone='" + numberPhone + '\'' +
                ", name='" + name + '\'' +
                ", avataImage=" + avataImage +
                '}';
    }
}
