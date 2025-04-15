package com.example.smsdetection.entity;

public class UserFeedback {
    private String title;
    private String category;
    private String content;
    private String contactInfo;
    private long submittedTime;

    public UserFeedback(String title, String category, String content, String contactInfo, long submittedTime) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.contactInfo = contactInfo;
        this.submittedTime = submittedTime;
    }
}
