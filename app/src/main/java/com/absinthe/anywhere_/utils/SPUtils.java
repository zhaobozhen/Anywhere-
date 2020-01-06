package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.model.Const;

public class SPUtils {
    private static String SPName;

    private static String getSPName() {
        if (SPName == null) {
            if (BuildConfig.DEBUG) {
                SPName = Const.SP_NAME_DEBUG;
            } else {
                SPName = Const.SP_NAME;
            }
        }
        return SPName;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE);
        if (defaultValue) {
            return sp.getBoolean(key, true);
        } else {
            return sp.getBoolean(key, false);
        }
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getSPName(), Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }
}
