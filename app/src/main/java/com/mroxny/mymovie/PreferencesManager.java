package com.mroxny.mymovie;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PreferencesManager {

    private static final String MY_SETTINGS = "MySettings" ;
    public static final String LOGGED_USER_ID = "logged_user";
    public static final String LOGGED_USER_NAME = "logged_user_name";
    public static final String LOGGED_USER_PASSWORD = "logged_user_password";



    public static void insertInt(String key, int data, @NonNull Context context){
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, data);
        editor.apply();
    }

    public static void insertString(String key, String data, @NonNull Context context){
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static int getInt(String key, int defaultValue, @NonNull Context context){
        SharedPreferences sharedPref;
        sharedPref =  context.getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

    public static String getString(String key, String defaultValue, @NonNull Context context){
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    public static void clearData(String key, @NonNull Context context){
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

}
