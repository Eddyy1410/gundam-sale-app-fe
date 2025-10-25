package com.huyntd.superapp.gundamshop_mobilefe.api;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.ConversationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;

import java.util.List;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
            .baseUrl("http://192.168.100.58:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);

    @POST("auth/log-in")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);

    @POST("auth/google-android")
    Call<ApiResponse<AuthenticationResponse>> loginGoogle(@Body GoogleTokenRequest request);


    @GET("api/products")
    Call<ApiResponse<PageResponse<ProductResponse>>> getProducts();

    @GET("api/products/{id}")
    Call<ApiResponse<ProductResponse>> getProduct(@Path("id") int id);

    @GET("api/products/category/{categoryId}")
    Call<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("user/")
    Call<ApiResponse<UserResponse>> register(@Body UserRegisterRequest request);

    @GET("api/conversations")
    Call<ApiResponse<ConversationResponse>> getConversations();
}
