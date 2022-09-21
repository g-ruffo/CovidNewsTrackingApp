package com.veltus.covidnewstracking.ObjectClass;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;

public class NewsArticle implements Serializable {

    private Bitmap image = null;

    private String title;

    private String author;

    private String date;

    private String website;

    private String imageUrl;


    public NewsArticle(Bitmap image, String title, String author, String date, String website, String imageUrl) {
        this.image = image;
        this.title = title;
        this.author = author;
        this.date = date;
        this.website = website;
        this.imageUrl = imageUrl;
    }

    public NewsArticle(String title, String author, String date, String website, String imageUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.website = website;
        this.imageUrl = imageUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getWebsite() {
        return website;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    /* Converts favoritesList items to a JavaObject that can be stored in SharedPreferences */
    public JSONObject getJSONObject() {
        JSONObject articleObject = new JSONObject();
        try {
            articleObject.put("title", title);
            articleObject.put("author", author);
            articleObject.put("date", date);
            articleObject.put("website", website);
            articleObject.put("imageUrl", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return articleObject;
    }

}


