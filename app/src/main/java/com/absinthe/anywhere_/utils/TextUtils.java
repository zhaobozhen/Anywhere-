package com.absinthe.anywhere_.utils;

import android.util.Log;

public class TextUtils {
    private static final String TAG = "TextUtils";
    public static String[] processResultString(String result) {
        String packageName, className;

        packageName = result.substring(result.indexOf(" u0 ") + 4, result.indexOf("/"));
        className = result.substring(result.indexOf("/") + 1, result.lastIndexOf(" "));

        Log.d(TAG, "packageName = " + packageName);
        Log.d(TAG, "className = " + className);

        if (String.valueOf(className.charAt(0)).equals(".")) {
            return new String[]{packageName, className, "shortClassName"};
        } else {
            return new String[]{packageName, className, "fullClassName"};
        }
    }
}
