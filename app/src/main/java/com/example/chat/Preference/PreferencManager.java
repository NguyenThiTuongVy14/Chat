package com.example.chat.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chat.firebase.AuthService;
import com.google.gson.Gson;

public class PreferencManager {
    private final SharedPreferences share;
    public PreferencManager(Context context){

        this.share = context.getSharedPreferences("Preference_NAME",Context.MODE_PRIVATE);
    }

    public void putBool(String key, Boolean value){
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public  Boolean getBool(String key){
        return  share.getBoolean(key,false);
    }
    public void putString(String key, String value){
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public  String getString(String key){
        return share.getString(key,null);
    }
    public void saveToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthServicePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(this);  // Chuyển đổi đối tượng AuthService thành JSON

        editor.putString("authService", json);  // Lưu JSON vào SharedPreferences
        editor.apply();
    }
    public static AuthService restoreFromSharedPreferences(Context context, AuthService.AuthCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthServicePrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("authService", null);

        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, AuthService.class);  // Chuyển JSON thành đối tượng AuthService
        } else {
            return new AuthService(callback);  // Nếu không có dữ liệu, tạo đối tượng AuthService mới
        }
    }


    public void clear(){
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.apply();
    }
}
