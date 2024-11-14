package com.example.chat.Model;

import android.graphics.Bitmap;

public class User {
    private String numberPhone;
    private String name;
    private Bitmap avataImage;

    public User() {
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public Bitmap getAvataImage() {
        return avataImage;
    }

    public void setAvataImage(Bitmap avataImage) {
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
