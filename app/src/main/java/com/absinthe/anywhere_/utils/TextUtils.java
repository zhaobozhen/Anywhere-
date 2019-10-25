package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.SerializableAnywhereEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextUtils {
    private static final Class klass = TextUtils.class;

    /**
     * process and obtain adb result
     * @param result return result
     */
    public static String[] processResultString(String result) {
        String packageName, className;
        int length = result.length();

        if (!result.contains(" u0 ") || result.indexOf(" u0 ") + 4 >= length - 1) {
            ToastUtil.makeText(R.string.toast_adb_result_process_failed);
            return null;
        }

        packageName = result.substring(result.indexOf(" u0 ") + 4, result.indexOf("/"));
        className = result.substring(result.indexOf("/") + 1, result.lastIndexOf(" "));

        LogUtil.d(klass, "packageName =", packageName);
        LogUtil.d(klass, "className =", className);

        if (String.valueOf(className.charAt(0)).equals(".")) {
            return new String[]{packageName, className, Const.SHORT_CLASS_NAME_TYPE + ""};
        } else {
            return new String[]{packageName, className, Const.FULL_CLASS_NAME_TYPE + ""};
        }
    }

    /**
     * get the app name by package name
     * @param context to get PackageManager
     * @param pkgName package name
     */
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

    /**
     * get current app package name
     * @param context to get ActivityManager
     */
    public static String getTopAppPackageName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

        if (am != null) {
            return am.getRunningAppProcesses().get(0).processName;
        }

        return "";
    }

    /**
     * get launch command of a item
     * @param item the item
     */
    public static String getItemCommand(AnywhereEntity item) {
        String cmd = null;
        int type = item.getAnywhereType();

        String packageName;
        String className;
        String urlScheme;
        int classNameType;

        if (type == AnywhereType.ACTIVITY) {
            packageName = item.getParam1();
            className = item.getParam2();
            classNameType = Integer.valueOf(item.getParam3());
            LogUtil.d(klass, "packageName =", packageName, "className =", className, "classNameType =" + classNameType);

            if (classNameType == Const.FULL_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + className;
            } else if (classNameType == Const.SHORT_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + packageName + className;
            }
        } else if (type == AnywhereType.URL_SCHEME) {
            urlScheme = item.getParam1();
            LogUtil.d(klass, "urlScheme =", urlScheme);

            if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                cmd = urlScheme;
            } else {
                cmd = "am start -a android.intent.action.VIEW -d " + urlScheme;
            }

        } else if (type == AnywhereType.MINI_PROGRAM) {
            //Todo
        } else {
            LogUtil.d(klass, "AnywhereType has problem.");
        }
        return cmd;
    }

    public static String getItemCommand(SerializableAnywhereEntity item) {
        String cmd = null;
        int type = item.getAnywhereType();

        String packageName;
        String className;
        String urlScheme;
        int classNameType;

        if (type == AnywhereType.ACTIVITY) {
            packageName = item.getmParam1();
            className = item.getmParam2();
            classNameType = Integer.valueOf(item.getmParam3());
            LogUtil.d(klass, "packageName =", packageName, "className =", className, "classNameType =" + classNameType);

            if (classNameType == Const.FULL_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + className;
            } else if (classNameType == Const.SHORT_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + packageName + className;
            }
        } else if (type == AnywhereType.URL_SCHEME) {
            urlScheme = item.getmParam1();
            LogUtil.d(klass, "urlScheme =", urlScheme);

            if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                cmd = urlScheme;
            } else {
                cmd = "am start -a android.intent.action.VIEW -d " + urlScheme;
            }

        } else if (type == AnywhereType.MINI_PROGRAM) {
            //Todo
        } else {
            LogUtil.d(klass, "AnywhereType has problem.");
        }
        return cmd;
    }

    /**
     * Get current date
     * @return date string
     */
    public static String getCurrFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
