package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.CreateOrderRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderRepository {

    private static OrderRepository instance;
    private String TAG = "ORDER_REPOSITORY_TAG";

    private OrderRepository() {}

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public LiveData<List<OrderResponse>> getOrdersByUserId(int userId) {
        MutableLiveData<List<OrderResponse>> data = new MutableLiveData<>();

        ApiClient.getApiService().getOrdersByUserId(userId)
                .enqueue(new Callback<ApiResponse<PageResponse<OrderResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<OrderResponse>>> call,
                                           Response<ApiResponse<PageResponse<OrderResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PageResponse<OrderResponse>> apiResponse = response.body();

                            Log.d("OrderRepository", "✅ Full Response: " + new Gson().toJson(apiResponse));

                            if (apiResponse.isSuccess()
                                    && apiResponse.getResult() != null
                                    && apiResponse.getResult().getContent() != null) {

                                data.setValue(apiResponse.getResult().getContent());
                                Log.d("OrderRepository", "✅ Loaded orders successfully for user: " + userId);

                            } else {
                                data.setValue(Collections.emptyList());
                                Log.w("OrderRepository", "⚠️ No orders found or API returned empty content");
                            }

                        } else {
                            try {
                                Log.e("OrderRepository", "❌ API failed: " + response.message() +
                                        " | Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<OrderResponse>>> call, Throwable t) {
                        Log.e("OrderRepository", "🚨 Error loading orders: " + t.getMessage(), t);
                    }
                });

        return data;
    }



    public LiveData<OrderResponse> getOrdersById (int orderId){
        MutableLiveData<OrderResponse> data = new MutableLiveData<>();

        ApiClient.getApiService().getOrderDetail(orderId).enqueue(new Callback<ApiResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderResponse>> call,
                                   Response<ApiResponse<OrderResponse>> response) {
                Log.d("OrderRepository", "🔹 Response code: " + response.code());
                Log.d("OrderRepository", "🔹 Response message: " + response.message());
                Log.d("OrderRepository", "🔹 Response headers: " + response.headers());

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

    public LiveData<List<OrderResponse>> getOrdersByStatus(int userId, String status) {
        MutableLiveData<List<OrderResponse>> data = new MutableLiveData<>();

        // Gọi API với page = 0, size = 100 (hoặc tùy theo BE config)
        ApiClient.getApiService().getOrdersByStatusAndUserId(userId, status)
                .enqueue(new Callback<ApiResponse<PageResponse<OrderResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<OrderResponse>>> call,
                                           Response<ApiResponse<PageResponse<OrderResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PageResponse<OrderResponse>> apiResponse = response.body();

                            Log.d("OrderRepository", "✅ Full Response: " + new Gson().toJson(apiResponse));

                            if (apiResponse.isSuccess()
                                    && apiResponse.getResult() != null
                                    && apiResponse.getResult().getContent() != null) {

                                data.setValue(apiResponse.getResult().getContent());
                                Log.d("OrderRepository", "✅ Loaded orders successfully for status: " + status);

                            } else {
                                data.setValue(Collections.emptyList());
                                Log.w("OrderRepository", "⚠️ No orders found or API returned empty content");
                            }

                        } else {
                            try {
                                Log.e("OrderRepository", "❌ API failed: " + response.message() +
                                        " | Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<OrderResponse>>> call, Throwable t) {
                        Log.e("OrderRepository", "🚨 Error loading orders by status: " + t.getMessage(), t);
                    }
                });

        return data;
    }

    public LiveData<OrderResponse> createOrder(CreateOrderRequest request) {
        MutableLiveData<OrderResponse> data = new MutableLiveData<>();

        ApiClient.getApiService().createOrder(request)
                .enqueue(new Callback<ApiResponse<OrderResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<OrderResponse>> call,
                                           Response<ApiResponse<OrderResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<OrderResponse> apiResponse = response.body();

                            Log.d("OrderRepository", "✅ CreateOrder Response: " + new Gson().toJson(apiResponse));

                            if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                data.setValue(apiResponse.getResult());
                                Log.d("OrderRepository", "✅ Order created successfully: " + apiResponse.getResult().getId());
                            } else {
                                data.setValue(null);
                                Log.w("OrderRepository", "⚠️ Order creation failed: Empty result");
                            }

                        } else {
                            try {
                                Log.e("OrderRepository", "❌ API failed: " + response.message() +
                                        " | Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<OrderResponse>> call, Throwable t) {
                        Log.e("OrderRepository", "🚨 Error creating order: " + t.getMessage(), t);
                        data.setValue(null);
                    }
                });

        return data;
    }

}

