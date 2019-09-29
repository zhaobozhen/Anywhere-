package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.SPUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.weishu.reflection.Reflection;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuMultiProcessHelper;
import moe.shizuku.api.ShizukuService;

public class AnywhereApplication extends Application {
    public static final String ACTION_SEND_BINDER = "moe.shizuku.client.intent.action.SEND_BINDER";
    public static final String TAG = "AnywhereApplication";
    public static String workingMode = null;

    @Override
    public void onCreate() {
        super.onCreate();
        String darkMode = SPUtils.getString(this, ConstUtil.SP_KEY_DARK_MODE);
        setTheme(darkMode);
    }

    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= 28)
            return Application.getProcessName();
        else {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                Method method = activityThread.getDeclaredMethod("currentProcessName");
                return (String) method.invoke(null);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean v3Failed;
    private static boolean v3TokenValid;

    public static boolean isShizukuV3Failed() {
        return v3Failed;
    }

    public static boolean isShizukuV3TokenValid() {
        return v3TokenValid;
    }

    public static void setShizukuV3TokenValid(boolean v3TokenValid) {
        AnywhereApplication.v3TokenValid = v3TokenValid;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Reflection.unseal(base);

        Log.d(TAG, "initialize " + ShizukuMultiProcessHelper.initialize(this, !getProcessName().endsWith(":test")));

        ShizukuClientHelper.setBinderReceivedListener(() -> {
            Log.d(TAG, "onBinderReceived");

            if (ShizukuService.getBinder() == null) {
                // ShizukuBinderReceiveProvider started without binder, should never happened
                Log.d(TAG, "binder is null");
                v3Failed = true;
            } else {
                try {
                    // test the binder first
                    ShizukuService.pingBinder();

                    if (Build.VERSION.SDK_INT < 23) {
                        String token = ShizukuClientHelper.loadPre23Token(base);
                        v3TokenValid = ShizukuService.setCurrentProcessTokenPre23(token);
                    }

                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_SEND_BINDER));
                } catch (Throwable tr) {
                    // blocked by SELinux or server dead, should never happened
                    Log.i(TAG, "can't contact with remote", tr);
                    v3Failed = true;
                }
            }
        });

        workingMode = SPUtils.getString(this, ConstUtil.SP_KEY_WORKING_MODE);
    }

    public static void setTheme(String mode) {
        switch (mode) {
            case "":
            case "off":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "on":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
        }
    }
}
