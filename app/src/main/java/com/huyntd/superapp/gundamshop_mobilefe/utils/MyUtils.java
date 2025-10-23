package com.huyntd.superapp.gundamshop_mobilefe.utils;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONObject;

public class MyUtils {

    public static final String USER_TYPE_GOOGLE = "Google";
    public static final String USER_TYPE_EMAIL = "Email";
    public static final String USER_TYPE_PHONE = "Phone";

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static long timestamp() {
        return System.currentTimeMillis();
    }


    public JSONObject decodeJwtPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
                return new JSONObject(payloadJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
