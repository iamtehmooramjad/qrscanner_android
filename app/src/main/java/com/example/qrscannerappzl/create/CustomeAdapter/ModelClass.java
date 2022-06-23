package com.example.qrscannerappzl.create.CustomeAdapter;

import android.graphics.drawable.Drawable;

public class ModelClass {
    String name;
    Drawable image;
    String url;


    public ModelClass(String name, Drawable image, String url) {
        this.name = name;
        this.image = image;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
