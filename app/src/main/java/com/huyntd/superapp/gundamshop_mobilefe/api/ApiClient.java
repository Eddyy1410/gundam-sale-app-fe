package com.huyntd.superapp.gundamshop_mobilefe.api;

import android.util.Log;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // này check ipconfig -> thay localhost = IPv4 Address của Wireless LAN adapter Wi-Fi
    private static final String BASE_URL = "http://10.87.28.61:8080/";

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

//            Gson gson = new GsonBuilder()
//                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        // Trả về instance duy nhất (3)
        return retrofit.create(ApiService.class);
    }

    public static void clearApiClient() {
        retrofit = null;
        token = null;
        Log.i("ApiClient", "Retrofit instance cleared after logout.");
        // SessionManager.getInstance().clearAuthToken();
    }

    // Provide an ApiService that never attaches Authorization header (for public endpoints)
    public static ApiService getUnauthService() {
        // Build a lightweight Retrofit instance without token interceptor
        Retrofit r = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
        return r.create(ApiService.class);
    }

    // Parse error body or ApiResponse to extract message
    public static String extractErrorMessage(retrofit2.Response<?> response, String defaultMsg) {
        try {
            if (response == null) return defaultMsg;

            // 1) If response has a body which is an ApiResponse, prefer that
            if (response.body() instanceof ApiResponse) {
                ApiResponse<?> body = (ApiResponse<?>) response.body();
                if (body != null) {
                    if (body.getCode() != 0) {
                        switch (body.getCode()) {
                            case 1011:
                                return "Mã xác thực không hợp lệ hoặc đã hết hạn.";
                            default:
                                break;
                        }
                    }
                    if (body.getMessage() != null && !body.getMessage().isEmpty()) return body.getMessage();
                }
            }

            // 2) Try to read the errorBody text (if any) and parse it as ApiResponse
            if (response.errorBody() != null) {
                String err = null;
                try (okhttp3.ResponseBody rb = response.errorBody()) {
                    err = rb.string();
                } catch (Exception e) {
                    // ignore read errors
                }

                if (err != null && !err.isEmpty()) {
                    try {
                        Gson g = new Gson();
                        ApiResponse<?> api = g.fromJson(err, ApiResponse.class);
                        if (api != null) {
                            if (api.getCode() != 0) {
                                switch (api.getCode()) {
                                    case 1011:
                                        return "Mã xác thực không hợp lệ hoặc đã hết hạn.";
                                    default:
                                        break;
                                }
                            }
                            if (api.getMessage() != null && !api.getMessage().isEmpty()) return api.getMessage();
                        }
                    } catch (Exception ex) {
                        // not ApiResponse or failed to parse — fall through to try regex
                    }

                    // Fallback: attempt to extract a "message" or "error" field from the raw JSON
                    String cleaned = extractSimpleJsonField(err, "message");
                    if (cleaned == null || cleaned.isEmpty()) cleaned = extractSimpleJsonField(err, "error");
                    if (cleaned != null && !cleaned.isEmpty()) {
                        // Limit length to avoid showing long raw JSON
                        if (cleaned.length() > 200) cleaned = cleaned.substring(0, 197) + "...";
                        return cleaned;
                    }

                    // As a last resort, if the raw text is short and non-JSON-ish, return it trimmed
                    String trimmed = err.trim();
                    if (trimmed.length() > 0 && trimmed.length() < 200 && !trimmed.startsWith("{")) {
                        return trimmed;
                    }

                    // Log raw error for debugging but do not surface full raw JSON to user
                    Log.d("ApiClient", "Raw error body: " + err);
                }
            }

        } catch (Exception e) {
            // ignore
        }
        return defaultMsg;
    }

    // Very small helper to extract a top-level string field value from JSON without a full parse
    private static String extractSimpleJsonField(String json, String fieldName) {
        if (json == null || json.isEmpty() || fieldName == null) return null;
        try {
            // look for patterns like "message":"some text"
            String pattern = "\"" + fieldName + "\"\s*:\s*\"";
            int idx = json.indexOf(pattern);
            if (idx >= 0) {
                int start = idx + pattern.length();
                StringBuilder sb = new StringBuilder();
                for (int i = start; i < json.length(); i++) {
                    char c = json.charAt(i);
                    if (c == '\\') {
                        // skip escaped next char
                        if (i + 1 < json.length()) {
                            sb.append(json.charAt(i + 1));
                            i++;
                        }
                    } else if (c == '"') {
                        break;
                    } else {
                        sb.append(c);
                    }
                    // guard
                    if (sb.length() > 1000) break;
                }
                return sb.toString();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
