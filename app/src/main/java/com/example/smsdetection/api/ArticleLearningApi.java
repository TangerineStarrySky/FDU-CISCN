package com.example.smsdetection.api;

import com.example.smsdetection.entity.ArticleLearning;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArticleLearningApi {
    @GET("/anti-fraud/articles/all")
    Call<List<ArticleLearning>> getArticles();

    @GET("/anti-fraud/articles/{id}")
    Call<ArticleLearning> getArticleDetails(@Path("id") long id);
}
