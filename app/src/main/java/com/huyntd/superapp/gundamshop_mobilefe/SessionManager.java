package com.huyntd.superapp.gundamshopmobilefe;

import android.content.Context;
import android.content.SharedPreferences;

// Dùng Singleton pattern
// Tạo file SessionManager.java
public class SessionManager {

    private static final String PREF_NAME = "GundamShopSession";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Singleton Pattern
    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext(); // Dùng application context để tránh memory leak
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    // --- Các phương thức quan trọng ---

    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply(); // Dùng apply() để lưu bất đồng bộ
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null); // Trả về null nếu không có token
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

}
