package com.huyntd.superapp.gundamshop_mobilefe.api;

import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.CreateOrderRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.ChangePasswordRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.DeleteAccountRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.LogoutRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.PaymentRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.ResetPasswordRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserProfileUpdateRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.CartResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ConversationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;

import java.util.List;
import java.util.Map;

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

    @POST("/auth/logout")
    Call<ApiResponse<Void>> logout(@Body LogoutRequest request);

    @POST("/auth/change-password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordRequest request);

    // Forgot / Reset password
    @POST("auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Query("email") String email);

    @POST("auth/reset-password")
    Call<ApiResponse<String>> resetPassword(@Body ResetPasswordRequest request);

    // Verify reset code (accepts email+code). Uses a Map payload in callers, so expose a Map<String,String> body here.
    @POST("auth/verify-reset-code")
    Call<ApiResponse<Void>> verifyResetCode(@Body java.util.Map<String, String> payload);

    @PUT("user/me")
    Call<ApiResponse<UserResponse>> updateMyProfile(@Body UserProfileUpdateRequest request);

    @retrofit2.http.HTTP(method = "DELETE", path = "user/me", hasBody = true)
    Call<ApiResponse<Void>> deleteMyAccount(@Body DeleteAccountRequest request);

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

    @GET("order/status/user/{id}")
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

    @POST("order")
    Call<ApiResponse<OrderResponse>> createOrder(@Body CreateOrderRequest request);

    //---------------------------------PAYMENT------------------------------------------
    @POST("payment/vnpay-create-payment")
    Call<ApiResponse<Map<String, String>>> createVNPAYPayment(@Body PaymentRequest request);
    @POST("payment/momo-create-payment")
    Call<ApiResponse<Map<String, String>>> createMomoPayment(@Body PaymentRequest request);

    //-----------------------------------CART-------------------------------------------
    @GET("cart/user/{id}")
    Call<ApiResponse<CartResponse>> getCartByUserId(@Path("id") int id);

    // --------------------------------CONVERSATION-------------------------------------
     @GET("api/conversations/list/{staffId}")
     Call<ApiResponse<List<ConversationResponse>>> getConversations(@Path("staffId") int id);

    // ----------------------------------MESSAGE----------------------------------------
    @GET("api/messages/{customerId}")
    Call<ApiResponse<List<MessageResponse>>> getMessages(@Path("customerId") int id);
}
