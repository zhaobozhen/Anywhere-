package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.SecurityUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.weishu.reflection.Reflection;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuMultiProcessHelper;
import moe.shizuku.api.ShizukuService;

public class AnywhereApplication extends Application {
    public static final String ACTION_SEND_BINDER = "moe.shizuku.client.intent.action.SEND_BINDER";
    @SuppressLint("StaticFieldLeak")
    public static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG &&
                !SecurityUtils.getSignatureMD5Value(this)
                .equals(SecurityUtils.getMySignatureMD5(this))) {
            SecurityUtils.exit();
        }

        sContext = getApplicationContext();
        GlobalValues.init(sContext);
        Settings.init();
    }

    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= 28)
            return Application.getProcessName();
        else {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                @SuppressLint("DiscouragedPrivateApi")
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

        LogUtil.d(this.getClass(), "initialize ", ShizukuMultiProcessHelper.initialize(this, !getProcessName().endsWith(":test")));

        ShizukuClientHelper.setBinderReceivedListener(() -> {
            LogUtil.d(this.getClass(), "onBinderReceived");

            if (ShizukuService.getBinder() == null) {
                // ShizukuBinderReceiveProvider started without binder, should never happened
                LogUtil.d(this.getClass(), "binder is null");
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
                    Log.i(this.getClass().getSimpleName(), "can't contact with remote", tr);
                    v3Failed = true;
                }
            }
        });
    }

}
