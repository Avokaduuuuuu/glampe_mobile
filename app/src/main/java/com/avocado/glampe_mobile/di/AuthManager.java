package com.avocado.glampe_mobile.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.google.gson.Gson;

public class AuthManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_RESPONSE = "auth_response";

    public static void saveAuthResponse(Context context, AuthUserResponse authResponse) {
        clearAuthResponse(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(authResponse);
        editor.putString(KEY_AUTH_RESPONSE, json);
        editor.apply();
    }

    public static AuthUserResponse getAuthResponse(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_AUTH_RESPONSE, null);
        if (json != null){
            Gson gson = new Gson();
            return gson.fromJson(json, AuthUserResponse.class);
        }

        return null;
    }

    public static void clearAuthResponse(Context context){
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
