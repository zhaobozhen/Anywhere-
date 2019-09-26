package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ConstUtil.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ConstUtil.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ConstUtil.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ConstUtil.SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, true);
    }
}
