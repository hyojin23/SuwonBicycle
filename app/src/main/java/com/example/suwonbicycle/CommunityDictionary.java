package com.example.suwonbicycle;

import android.net.Uri;

public class CommunityDictionary {

    private String title;
    private String time;
    private String content;
    private Uri image;




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CommunityDictionary(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public CommunityDictionary(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public CommunityDictionary(String title, String time, String content, Uri image) {
        this.title = title;
        this.time = time;
        this.content = content;
        this.image = image;
    }
    public CommunityDictionary(String title, String time, String content) {
        this.title = title;
        this.time = time;
        this.content = content;
    }
}

