package com.huyntd.superapp.gundamshopmobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.Auth;
import com.huyntd.superapp.gundamshopmobilefe.R;
import com.huyntd.superapp.gundamshopmobilefe.SessionManager;
import com.huyntd.superapp.gundamshopmobilefe.api.ApiService;
import com.huyntd.superapp.gundamshopmobilefe.databinding.ActivityLoginEmailBinding;
import com.huyntd.superapp.gundamshopmobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshopmobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshopmobilefe.models.response.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;

    private AuthenticationResponse loginResult = new AuthenticationResponse();

    String TAG = "LOGIN_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginOptionsActivity();
            }
        });

        binding.loginBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService.apiService.login(AuthenticationRequest.builder()
                        .email(binding.emailEt.getText().toString())
                        .password(binding.passwordEt.getText().toString())
                        .build()).enqueue(new Callback<ApiResponse<AuthenticationResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AuthenticationResponse>> call, Response<ApiResponse<AuthenticationResponse>> response) {
                        Log.i(TAG, "onResponse: "+response.body().toString());
                        SessionManager.getInstance(LoginEmailActivity.this).saveAuthToken(response.body().getResult().getToken());
                        Toast.makeText(LoginEmailActivity.this, SessionManager.getInstance(LoginEmailActivity.this).getAuthToken(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AuthenticationResponse>> call, Throwable t) {
                        Toast.makeText(LoginEmailActivity.this, "Error occured!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
            }
        });

    }

    private void startLoginOptionsActivity() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

}