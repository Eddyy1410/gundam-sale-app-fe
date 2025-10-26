package com.huyntd.superapp.gundamshop_mobilefe.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.10.10.251:8080/";
    private static Retrofit retrofit = null;
    private static String token = null;

    // 🔑 Gọi khi bạn có token (sau khi login)
    public static void setToken(String newToken) {
        token = newToken;
        retrofit = null; // reset để build lại Retrofit có token
    }

    public static ApiService getApiService() {
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}
