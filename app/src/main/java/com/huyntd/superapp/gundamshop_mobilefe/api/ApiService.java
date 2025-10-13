package com.huyntd.superapp.gundamshopmobilefe.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huyntd.superapp.gundamshopmobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshopmobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshopmobilefe.models.response.AuthenticationResponse;

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
            .baseUrl("http://192.168.100.58:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);

    @POST("auth/log-in")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);
}
