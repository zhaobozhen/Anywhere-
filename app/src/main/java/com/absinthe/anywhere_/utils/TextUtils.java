package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.main.MainActivity;

public class TextUtils {
    private static final String TAG = "TextUtils";

    public static String[] processResultString(String result) {
        String packageName, className;
        int length = result.length();

        if (!result.contains(" u0 ") || result.indexOf(" u0 ") + 4 >= length - 1) {
            Toast.makeText(MainActivity.getInstance(), R.string.toast_adb_result_process_failed, Toast.LENGTH_SHORT).show();
            return null;
        }

        packageName = result.substring(result.indexOf(" u0 ") + 4, result.indexOf("/"));
        className = result.substring(result.indexOf("/") + 1, result.lastIndexOf(" "));

        Log.d(TAG, "packageName = " + packageName);
        Log.d(TAG, "className = " + className);

        if (String.valueOf(className.charAt(0)).equals(".")) {
            return new String[]{packageName, className, Const.SHORT_CLASS_NAME_TYPE + ""};
        } else {
            return new String[]{packageName, className, Const.FULL_CLASS_NAME_TYPE + ""};
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

    public static String getTopAppPackageName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

        if (am != null) {
            return am.getRunningAppProcesses().get(0).processName;
        }

        return "";
    }
}
