package com.example.smsdetection.entity;

public class Video {
    private String title;
    private String thumbnailUrl;
    private String url;

    // 构造函数
    public Video(String title, String thumbnailUrl, String url) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.url = url;
    }

    // Getter 方法
    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }
}