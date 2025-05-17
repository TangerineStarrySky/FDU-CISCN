package com.example.smsdetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smsdetection.ArticleDetailActivity;
import com.example.smsdetection.R;
import com.example.smsdetection.VideoLearningActivity;
import com.example.smsdetection.adapter.LearningAdapter;
import com.example.smsdetection.api.ApiClient;
import com.example.smsdetection.api.ArticleLearningApi;
import com.example.smsdetection.entity.ArticleLearning;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LearningActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
//    private TabLayout tabLayout;
    private ListView learningDescriptionListView;

    private List<ArticleLearning> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("反诈知识学习");
        TextView tv_history = findViewById(R.id.tv_history);
        tv_history.setText("");

        learningDescriptionListView = findViewById(R.id.learning_description);
        learningDescriptionListView.setOnItemClickListener(this);

//        tabLayout = findViewById(R.id.tabLayout);

        // 监听 Tab 切换
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(@NonNull TabLayout.Tab tab) {
//                if (tab.getPosition() == 1) { // 第2个 Tab（索引从 0 开始）
//                    Intent intent = new Intent(LearningActivity.this, VideoLearningActivity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {}
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                // 这里可以添加“重复点击”的逻辑
//            }
//        });

        // 调用 Retrofit 请求 API 数据
        fetchArticles();

        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 使用 Retrofit 获取反诈内容数据
    private void fetchArticles() {
        Retrofit retrofit = ApiClient.getClient();
        ArticleLearningApi apiService = retrofit.create(ArticleLearningApi.class);
        Call<List<ArticleLearning>> call = apiService.getArticles();

        call.enqueue(new Callback<List<ArticleLearning>>() {
            @Override
            public void onResponse(Call<List<ArticleLearning>> call, Response<List<ArticleLearning>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    articles = response.body();
                    if (articles.isEmpty()) {
                        Log.w("LearningActivity", "返回的文章数据为空");
                    } else {
                        LearningAdapter adapter = new LearningAdapter(LearningActivity.this, articles);
                        learningDescriptionListView.setAdapter(adapter);
                    }
                } else {
                    Log.e("LearningActivity", "请求失败或返回空数据");
                }
            }

            @Override
            public void onFailure(Call<List<ArticleLearning>> call, Throwable t) {
                Log.e("LearningActivity", "API请求失败", t);
                // 错误处理
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(LearningActivity.this, ArticleDetailActivity.class);
        intent.putExtra("article_id", articles.get(position).getId());
        startActivity(intent);
    }
}