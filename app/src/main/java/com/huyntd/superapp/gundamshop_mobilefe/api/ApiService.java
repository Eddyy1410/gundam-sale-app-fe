package com.huyntd.superapp.gundamshop_mobilefe.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
            .baseUrl("http://10.87.23.0:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);

    @POST("auth/log-in")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);

    @POST("auth/google-android")
    Call<ApiResponse<AuthenticationResponse>> loginGoogle(@Body GoogleTokenRequest request);
}
