package com.example.smsdetection.merge;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smsdetection.R;
import com.example.smsdetection.api.ApiClient;
import com.example.smsdetection.api.FeedbackApi;
import com.example.smsdetection.entity.UserFeedback;
import com.example.smsdetection.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MIN_FEEDBACK_LENGTH = 10;
    private EditText etFeedback, etTitle, etContactInfo;
    private Spinner spCategory;
    private FeedbackApi feedbackApi;

    // 分类数据
    private final String[] categories = {
            "请选择反馈类型",
            "功能建议",
            "问题反馈",
            "用户体验",
            "其他"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinner();
        initApiClient();

    }

    private void initViews() {
        etFeedback = findViewById(R.id.et_feedback);
        etTitle = findViewById(R.id.et_title);
        etContactInfo = findViewById(R.id.et_contact_info);
        spCategory = findViewById(R.id.sp_category);

        findViewById(R.id.btn_submit_feedback).setOnClickListener(this);
        findViewById(R.id.ic_back).setOnClickListener(this);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("用户反馈");
        findViewById(R.id.tv_history).setVisibility(View.GONE); // 隐藏历史按钮
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }


    private void initApiClient() {
        feedbackApi = ApiClient.getClient().create(FeedbackApi.class);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ic_back) {
            finish();
        } else if (id == R.id.btn_submit_feedback) {
            submitFeedback();
        }
    }

    private void submitFeedback() {
        // 获取输入内容
        String title = etTitle.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        String content = etFeedback.getText().toString().trim();
        String contactInfo = etContactInfo.getText().toString().trim();

        // 验证输入
        if (!validateInput(title, category, content, contactInfo)) {
            return;
        }

        // 创建反馈对象
        UserFeedback feedback = new UserFeedback(
                title,
                category,
                content,
                contactInfo,
                System.currentTimeMillis()
        );

        // 提交反馈
        submitFeedbackToServer(feedback);
    }

    private boolean validateInput(String title, String category, String content, String contactInfo) {
        // 验证标题
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("请输入反馈标题");
            etTitle.requestFocus();
            return false;
        }

        // 验证分类
        if (category.equals(categories[0])) {
            ToastUtil.show(this, "请选择反馈类型");
            return false;
        }

        // 验证内容
        if (TextUtils.isEmpty(content)) {
            etFeedback.setError("请输入反馈内容");
            etFeedback.requestFocus();
            return false;
        }

        if (content.length() < MIN_FEEDBACK_LENGTH) {
            etFeedback.setError("反馈内容至少需要" + MIN_FEEDBACK_LENGTH + "个字符");
            etFeedback.requestFocus();
            return false;
        }

        // 验证联系方式
        if (!TextUtils.isEmpty(contactInfo) && !isValidContactInfo(contactInfo)) {
            etContactInfo.setError("请输入有效的邮箱或手机号");
            etContactInfo.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidContactInfo(String contactInfo) {
        return isValidEmail(contactInfo) || isValidPhone(contactInfo);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;

        // 中国手机号正则（11位，以1开头，第二位通常3-9）
        String regex = "^(?:(?:\\+|00)86)?1[3-9]\\d{9}$";
        return phone.matches(regex);
    }

    private void submitFeedbackToServer(UserFeedback feedback) {
        feedbackApi.submitUserFeedback(feedback).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.isSuccessful()) {
                    handleSuccess();
                } else {
                    handleError("提交失败: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleError("网络错误: " + t.getMessage());
            }
        });
    }

    private void handleSuccess() {
        clearForm();
        ToastUtil.show(this, "感谢您的反馈~");
        Log.i("Feedback", "提交成功");
    }

    private void handleError(String errorMsg) {
        Log.e("Feedback", errorMsg);
        ToastUtil.show(this, "提交失败，请稍后重试");
    }

    private void clearForm() {
        etTitle.setText("");
        etFeedback.setText("");
        etContactInfo.setText("");
        spCategory.setSelection(0);
    }

}
