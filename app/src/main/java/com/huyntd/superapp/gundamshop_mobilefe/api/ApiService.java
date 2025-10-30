package com.huyntd.superapp.gundamshop_mobilefe.api;

import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

//    Gson gson = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .create();

    // 2. KHỞI TẠO RETROFIT
//    ApiService apiService = new Retrofit.Builder()
//            // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
//            .baseUrl("http://192.168.137.1:8080/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService.class);

    //-------------------------------AUTHENTICATION--------------------------------------
    // Mấy cái login, register ko cần @SkipAuth. Do cơ chế bên ApiClient ko có token thì ko cần auth
    @POST("auth/log-in")
    Call<ApiResponse<AuthenticationResponse>> login(@Body AuthenticationRequest request);

    @POST("auth/google-android")
    Call<ApiResponse<AuthenticationResponse>> loginGoogle(@Body GoogleTokenRequest request);

    @POST("user/")
    Call<ApiResponse<UserResponse>> register(@Body UserRegisterRequest request);

    @GET("user/myInfo")
    Call<ApiResponse<UserResponse>> getInfo();

    //-----------------------------------PRODUCT------------------------------------------
    @GET("api/products")
    @SkipAuth
    Call<ApiResponse<PageResponse<ProductResponse>>> getProducts();

    @GET("api/products/{id}")
    Call<ApiResponse<ProductResponse>> getProduct(@Path("id") int id);

    @GET("api/products/category/{categoryId}")
    Call<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("size") int size);

    // -----------------------------------ORDER-------------------------------------------
    @GET("order/user/{id}")
    Call<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUserId(@Path("id") int id);

    @GET("order/user/{id}")
    Call<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatusAndUserId(@Path("id") int id,
            @Query("status") String status);

    @GET("order/{id}")
    Call<ApiResponse<OrderResponse>> getOrderDetail(@Path("id") int id);

    @GET("order/today")
    Call<ApiResponse<PageResponse<OrderResponse>>> getOrdersToday(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir,
            @Query("status") String status
    );
    @PUT("order/{id}/status")
    Call<ApiResponse<OrderResponse>> updateOrderStatus(
            @Path("id") int id,
            @Query("status") String status
    );

    // --------------------------------CONVERSATION-------------------------------------
    // @GET("api/conversations")
    // Call<ApiResponse<ConversationResponse>> getConversations();




//    -------------------------------DASHBOARD--------------------------------------
    @GET("/order/todays/count")
    Call<ApiResponse<Long>> getTodaysOrderCount();

    @GET("/order/pending/count")
    Call<ApiResponse<Long>> getPendingOrdersCount();

    @GET("/api/products/low-stock-count")
    Call<Long> getLowStockCount(@Query("threshold") int threshold);
}
