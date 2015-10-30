package com.foodie.recipe.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rajnish on 27/9/15.
 */
public class AppPreference {
    public static final String APP_PREFERENCE_KEY = "APP_PREFERENCE_KEY";
    private static AppPreference sInstance;
    private SharedPreferences mPref;

    public AppPreference(Context cntx){
        mPref = PreferenceManager
                .getDefaultSharedPreferences(cntx);

    }

    public static AppPreference getInstance(Context cntx) {
        if (sInstance == null) {
            sInstance = new AppPreference(cntx);
        }
        return sInstance;
    }


    public void putInteger(String key, int value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public int getInteger(String key,int value){
        int returnValue = mPref.getInt(key, value);
        return returnValue;
    }


    public void putString(String key, String value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key,String value){
        String returnValue = mPref.getString(key, value);
        return returnValue;
    }


    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean value){
        boolean returnValue = mPref.getBoolean(key, value);
        return returnValue;
    }
}
