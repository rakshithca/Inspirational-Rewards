package com.inspiration.inspirationrewards.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aspavanspl on 21/07/16.
 */
public class StoredData {

    private static final String SQLITE_NAME = "inspiration";

    // for saving and geeting the result....
    public static boolean saveString(Context context, String type, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SQLITE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(type, value);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SQLITE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static boolean saveBoolean(Context context, String type, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SQLITE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(type, value);
        return editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SQLITE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

}
