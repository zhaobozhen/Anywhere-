package com.absinthe.anywhere_.utils;

import android.util.Log;

public class LogUtil {
    public static void v(Class klass, String... contents) {
        StringBuilder sb = new StringBuilder();

        for (String str : contents) {
            sb.append(str).append(" ");
        }

        Log.v(klass.getSimpleName(), sb.toString());
    }

    public static void d(Class klass, String... contents) {
        StringBuilder sb = new StringBuilder();

        for (String str : contents) {
            sb.append(str).append(" ");
        }

        Log.d(klass.getSimpleName(), sb.toString());
    }

    public static void i(Class klass, String... contents) {
        StringBuilder sb = new StringBuilder();

        for (String str : contents) {
            sb.append(str).append(" ");
        }

        Log.i(klass.getSimpleName(), sb.toString());
    }

    public static void e(Class klass, String... contents) {
        StringBuilder sb = new StringBuilder();

        for (String str : contents) {
            sb.append(str).append(" ");
        }

        Log.e(klass.getSimpleName(), sb.toString());
    }

    public static void w(Class klass, String... contents) {
        StringBuilder sb = new StringBuilder();

        for (String str : contents) {
            sb.append(str).append(" ");
        }

        Log.w(klass.getSimpleName(), sb.toString());
    }

    public static void runningHere(Class klass) {
        Log.d(klass.getSimpleName(), klass.getSimpleName() + " is running here");
    }
}
