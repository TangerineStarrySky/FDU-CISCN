package com.example.smsdetection.entity;

public class DetectionFeedback {
    private String content;        // 短信内容
    private String detectedResult;// 系统识别结果
    private String userResult;    // 用户判断结果
    private String comment;       // 备注
    private String feedbackTime;  // 反馈时间

    public void setContent(String content) {
        this.content = content;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDetectedResult(String detectedResult) {
        this.detectedResult = detectedResult;
    }

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public void setUserResult(String userResult) {
        this.userResult = userResult;
    }
}