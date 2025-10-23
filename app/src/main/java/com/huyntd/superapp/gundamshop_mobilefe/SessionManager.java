package com.huyntd.superapp.gundamshop_mobilefe;

import android.content.Context;
import android.content.SharedPreferences;

import com.huyntd.superapp.gundamshop_mobilefe.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Dùng Singleton pattern
// Tạo file SessionManager.java
public class SessionManager {

    private static final String PREF_NAME = "GundamShopSession";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private final MyUtils myUtils = new MyUtils();



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

    public String getRole(){
        String token = getAuthToken();
        if (token == null) return null;

        JSONObject payload = myUtils.decodeJwtPayload(token);
        if (payload == null) return null;

        return payload.optString("role", null); // Khi payload chỉ có 1 role
    }


    public boolean hasRole(String role) {
        String token = getAuthToken();
        if (token == null) return false;

        JSONObject payload = myUtils.decodeJwtPayload(token);
        if (payload == null) return false;

        // Trường hợp token có mảng "roles": ["ADMIN", "USER"]
        if (payload.has("roles")) {
            JSONArray rolesArray = payload.optJSONArray("roles");
            if (rolesArray != null) {
                for (int i = 0; i < rolesArray.length(); i++) {
                    try {
                        if (role.equalsIgnoreCase(rolesArray.getString(i))) {
                            return true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Trường hợp token chỉ có "role": "ADMIN"
        String singleRole = payload.optString("role", null);
        return role.equalsIgnoreCase(singleRole);
    }


}
