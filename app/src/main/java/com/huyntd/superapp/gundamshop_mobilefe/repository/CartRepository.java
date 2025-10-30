package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.CartResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartRepository {
    private static CartRepository instance;
    private String TAG = "CART_REPOSITORY_TAG";

    private CartRepository() {}

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public LiveData<CartResponse> getCartByUserId(int userId) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        ApiClient.getApiService().getCartByUserId(userId)
                .enqueue(new Callback<ApiResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponse>> call,
                                           Response<ApiResponse<CartResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<CartResponse> apiResponse = response.body();

                            Log.d("CartRepository", "✅ Full Response: " + new Gson().toJson(apiResponse));

                            if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                data.setValue(apiResponse.getResult());
                                Log.d("CartRepository", "✅ Loaded cart successfully for user: " + userId);
                            } else {
                                data.setValue(null);
                                Log.w("CartRepository", "⚠️ No cart found or API returned null result");
                            }

                        } else {
                            try {
                                Log.e("CartRepository", "❌ API failed: " + response.message() +
                                        " | Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                        Log.e("CartRepository", "🚨 Error loading cart: " + t.getMessage(), t);
                    }
                });

        return data;
    }

}
