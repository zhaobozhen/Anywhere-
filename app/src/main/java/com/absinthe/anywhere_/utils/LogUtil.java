package com.absinthe.anywhere_.utils;

import android.util.Log;

public class LogUtil {
    public static void v(Class klass, Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            sb.append(obj.toString()).append(" ");
        }

        Log.v(klass.getSimpleName(), sb.toString());
    }

    public static void d(Class klass, Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            sb.append(obj.toString()).append(" ");
        }

        Log.d(klass.getSimpleName(), sb.toString());
    }

    public static void i(Class klass, Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            sb.append(obj.toString()).append(" ");
        }

        Log.i(klass.getSimpleName(), sb.toString());
    }

    public static void e(Class klass, Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            sb.append(obj.toString()).append(" ");
        }

        Log.e(klass.getSimpleName(), sb.toString());
    }

    public static void w(Class klass, Object... contents) {
        StringBuilder sb = new StringBuilder();

        for (Object obj : contents) {
            sb.append(obj.toString()).append(" ");
        }

        Log.w(klass.getSimpleName(), sb.toString());
    }

    public static void runningHere(Class klass) {
        Log.d(klass.getSimpleName(), klass.getSimpleName() + " is running here");
    }
}
