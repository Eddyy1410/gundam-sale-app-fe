package com.huyntd.superapp.gundamshop_mobilefe.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
    private static final String BASE_URL = "http://192.168.100.58:8080/";

    // Biến instance của Retrofit (ban đầu là null) (1)
    private static Retrofit retrofit = null;
    private static String token = null;

    // 🔑 Gọi khi bạn có token (sau khi login)
    public static void setToken(String newToken) {
        token = newToken;
        retrofit = null; // reset để build lại Retrofit có token
    }

    // getApiService dùng design pattern Singleton ---> đảm bảo retrofit có 1 instance duy nhất
    // Singleton là đảm bảo chỉ một thể hiện (instance) của một lớp được tạo ra
    public static ApiService getApiService() {

        // Kiểm tra: Nếu chưa có instance, thì tạo ra (2)
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            if (token != null) {
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .method(original.method(), original.body());

                        Log.d("ApiClient", "✅ Added token: Bearer " + token);

                        return chain.proceed(requestBuilder.build());
                    }
                });
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }

        // Trả về instance duy nhất (3)
        return retrofit.create(ApiService.class);
    }
}
