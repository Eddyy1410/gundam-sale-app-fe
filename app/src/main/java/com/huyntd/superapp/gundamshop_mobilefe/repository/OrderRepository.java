package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderRepository {

    private static OrderRepository instance;

    private OrderRepository() {}

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public LiveData<List<OrderResponse>> getOrdersByUserId(int userId) {
        MutableLiveData<List<OrderResponse>> data = new MutableLiveData<>();

        ApiService.apiService.getOrdersByUserId(userId).enqueue(new Callback<ApiResponse<PageResponse<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<OrderResponse>>> call,
                                   Response<ApiResponse<PageResponse<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // ✅ Lấy danh sách đơn hàng từ PageResponse
                    data.setValue(response.body().getResult().getContent());
                    Log.d("OrderRepository", "✅ Loaded orders successfully for user: " + userId);
                } else {
                    Log.e("OrderRepository", "❌ API failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<OrderResponse>>> call, Throwable t) {
                Log.e("OrderRepository", "🚨 Error loading orders: " + t.getMessage());
            }
        });

        return data;
    }

    public LiveData<OrderResponse> getOrdersById (int orderId){
        MutableLiveData<OrderResponse> data = new MutableLiveData<>();

        ApiService.apiService.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderResponse>> call,
                                   Response<ApiResponse<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // ✅ Lấy danh sách đơn hàng từ PageResponse
                    data.setValue(response.body().getResult());
                    Log.d("OrderRepository", "✅ Loaded orders successfully for user: " + orderId);
                } else {
                    Log.e("OrderRepository", "❌ API failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderResponse>> call, Throwable t) {
                Log.e("OrderRepository", "🚨 Error loading orders: " + t.getMessage());
            }
        });
        return data;
    }

}

