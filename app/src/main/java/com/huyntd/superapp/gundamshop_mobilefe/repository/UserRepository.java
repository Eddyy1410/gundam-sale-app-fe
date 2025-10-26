package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private static UserRepository instance;

    private UserRepository() {}

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    // Hàm gọi API trả về LiveData
    public LiveData<UserResponse> getInfo() {
        MutableLiveData<UserResponse> data = new MutableLiveData<>();

        ApiClient.getApiService().getInfo().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    data.setValue(response.body().getResult());
                } else {
                    Log.e(TAG, "API failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });

        return data;
    }
}

