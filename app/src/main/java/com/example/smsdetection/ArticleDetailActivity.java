package com.example.smsdetection;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smsdetection.api.ApiClient;
import com.example.smsdetection.api.ArticleLearningApi;
import com.example.smsdetection.entity.ArticleLearning;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView tvDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tv_title);
        tvDetail = findViewById(R.id.tv_detail);

        // 获取文章 ID 并通过 API 加载文章内容
        long articleId= getIntent().getLongExtra("article_id", -1);
        if (articleId != -1) {
            fetchArticleDetails(articleId);
        }
    }

    // 通过 Retrofit 获取文章详情
    private void fetchArticleDetails(long articleId) {
        Retrofit retrofit = ApiClient.getClient();
        ArticleLearningApi apiService = retrofit.create(ArticleLearningApi.class);
        Call<ArticleLearning> call = apiService.getArticleDetails(articleId);

        call.enqueue(new Callback<ArticleLearning>() {
            @Override
            public void onResponse(Call<ArticleLearning> call, Response<ArticleLearning> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArticleLearning article = response.body();

                    Log.d("ArticleDetail", "文章标题：" + article.getTitle());
                    Log.d("ArticleDetail", "文章内容：" + article.getDetail());
                    // 填充界面内容
                    tvTitle.setText(article.getTitle());
                    tvDetail.setText(article.getDetail());
                } else Log.d("ArticleDetail", "response 不成功或为空");
            }

            @Override
            public void onFailure(Call<ArticleLearning> call, Throwable t) {
                // 错误处理
            }
        });
    }
}