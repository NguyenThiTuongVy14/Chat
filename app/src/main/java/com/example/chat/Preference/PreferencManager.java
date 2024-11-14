package com.example.chat.Preference;

import android.content.Context;
import android.content.SharedPreferences;

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

    public void clear(){
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.apply();
    }
}
