package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityRegisterEmailBinding;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterEmailActivity extends AppCompatActivity {

    ActivityRegisterEmailBinding binding;

    static final String TAG = "REGISTER_EMAIL_TAG";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cơ chế của finish() là "hủy bỏ" (destroy) Activity hiện tại và xóa nó khỏi "chồng" Activity (Activity Stack)
                // dẫn đến việc Activity nằm ngay bên dưới nó trong chồng sẽ được hiển thị ra.
                finish();
            }
        });

        binding.haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    String email, password, cPassword, name, phone;

    void validateData() {

        email = binding.emailEt.getText().toString();
        password = binding.passwordEt.getText().toString();
        cPassword = binding.cPasswordEt.getText().toString();
        name = binding.phoneEt.getText().toString();
        phone = binding.phoneEt.getText().toString();

        Log.d(TAG, "validateData: Email:"+email);
        Log.d(TAG, "validateData: Password: "+password);
        Log.d(TAG, "validateData: Confirm Password: "+cPassword);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError("Invalid Email Pattern");
            binding.emailEt.requestFocus();

        } else if (password.isEmpty()) {
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();

        } else if (!password.equals(cPassword)) {
            binding.cPasswordEt.setError("Password does not match");
            binding.cPasswordEt.requestFocus();

        } else {
            ApiService.apiService.register(UserRegisterRequest.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .phone(phone)
                    .build()).enqueue(new Callback<ApiResponse<UserResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                    Log.i(TAG, "onResponse: "+response.body().toString());
                    Toast.makeText(RegisterEmailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                    Toast.makeText(RegisterEmailActivity.this, "Error occured!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        }

    }

}