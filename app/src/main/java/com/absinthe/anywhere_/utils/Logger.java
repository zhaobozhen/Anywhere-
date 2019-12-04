package com.absinthe.anywhere_.utils;

import android.util.Log;

/**
 * Logger
 */
public class Logger {

    private static boolean isDebugMode;

    public static void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

    public static int v(Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            if (obj == null) {
                sb.append("NULL").append(" ");
            } else {
                sb.append(obj.toString()).append(" ");
            }
        }

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return isDebugMode ? Log.v(TAG, sb.toString()) : -1;
    }

    public static int d(Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            if (obj == null) {
                sb.append("NULL").append(" ");
            } else {
                sb.append(obj.toString()).append(" ");
            }
        }

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return isDebugMode ? Log.d(TAG, sb.toString()) : -1;
    }

    public static int i(Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            if (obj == null) {
                sb.append("NULL").append(" ");
            } else {
                sb.append(obj.toString()).append(" ");
            }
        }

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return Log.i(TAG, sb.toString());
    }

    public static int e(Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            if (obj == null) {
                sb.append("NULL").append(" ");
            } else {
                sb.append(obj.toString()).append(" ");
            }
        }

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return Log.e(TAG, sb.toString());
    }

    public static int w(Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            if (obj == null) {
                sb.append("NULL").append(" ");
            } else {
                sb.append(obj.toString()).append(" ");
            }
        }

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return isDebugMode ? Log.w(TAG, sb.toString()) : -1;
    }

    public static int runningHere() {

        String clsName = new Throwable().getStackTrace()[1].getClassName();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        String TAG = "|" + clsName + "#" + new Throwable().getStackTrace()[1].getMethodName() + "()|";

        return isDebugMode ? Log.d(TAG, " is running here") : -1;
    }
}
