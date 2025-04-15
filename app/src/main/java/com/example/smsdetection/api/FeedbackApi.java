package com.example.smsdetection.api;

import com.example.smsdetection.entity.DetectionFeedback;
import com.example.smsdetection.entity.UserFeedback;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FeedbackApi {
    @POST("/detectionFeedback/submit") // 替换成你的实际 URL 路径
    Call<Void> submitFeedback(@Body DetectionFeedback request);

    @POST("/userFeedback/submit")
    Call<Void> submitUserFeedback(@Body UserFeedback request);
}
