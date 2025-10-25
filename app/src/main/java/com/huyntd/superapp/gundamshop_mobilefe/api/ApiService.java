package com.huyntd.superapp.gundamshop_mobilefe.api;

import android.content.Context;

import com.huyntd.superapp.gundamshop_mobilefe.MyApplication;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

//    Gson gson = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .create();

    // 1. TẠO OKHTTP CLIENT
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                // Lấy Application Context tĩnh
                Context context = MyApplication.getAppContext();

                // Lấy Token từ SessionManager
                // CHÚ Ý: Đảm bảo rằng getAppContext() không trả về NULL
                String token = null;
                if (context != null) {
                    token = SessionManager.getInstance(context).getAuthToken();
                }

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                // Thêm Header nếu Token tồn tại
                if (token != null && !token.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }

                return chain.proceed(requestBuilder.build());
            })
            // Thêm Logging Interceptor (Tùy chọn, nên thêm)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();


    // 2. KHỞI TẠO RETROFIT
//    ApiService apiService = new Retrofit.Builder()
//            // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
//            .baseUrl("http://192.168.137.1:8080/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService.class);

    //-------------------------------AUTHENTICATION--------------------------------------
    @POST("auth/log-in")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);

    @POST("auth/google-android")
    Call<ApiResponse<AuthenticationResponse>> loginGoogle(@Body GoogleTokenRequest request);

    @POST("user/")
    Call<ApiResponse<UserResponse>> register(@Body UserRegisterRequest request);

    //-----------------------------------PRODUCT------------------------------------------
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

    //-----------------------------------ORDER-------------------------------------------
    @GET("order/user/{id}")
    Call<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUserId(@Path("id") int id);

    @GET("order/user/{id}")
    Call<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatusAndUserId(@Path("id") int id, @Query("status") String status);

    @GET("order/{id}")
    Call<ApiResponse<OrderResponse>> getOrderDetail(@Path("id") int id);

    //--------------------------------CONVERSATION-------------------------------------
//    @GET("api/conversations")
//    Call<ApiResponse<ConversationResponse>> getConversations();
}
