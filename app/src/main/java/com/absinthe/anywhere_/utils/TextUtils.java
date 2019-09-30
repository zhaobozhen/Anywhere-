package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
            return new String[]{packageName, className, ConstUtil.SHORT_CLASS_NAME_TYPE + ""};
        } else {
            return new String[]{packageName, className, ConstUtil.FULL_CLASS_NAME_TYPE + ""};
        }
    }

    public static String getAppName(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            return info.loadLabel(pm).toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
