package com.example.newsapp;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class newsEntry {

    private String newsTitle;
    private String pubDate;
    private String description;
    private String imageURL;
    private Bitmap imageBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public String toString() {
        return "Title:"+newsTitle+
                "\npublished Date:"+pubDate+
                "\nDescription:"+description+
                "\nImage URL:"+imageURL;
    }
}
