package com.absinthe.anywhere_;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.absinthe.anywhere_.database.AnywhereRepository;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;
import com.absinthe.anywhere_.utils.manager.ShizukuHelper;
import com.absinthe.anywhere_.utils.timber.ReleaseTree;
import com.absinthe.anywhere_.utils.timber.ThreadAwareDebugTree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jonathanfinerty.once.Once;
import me.weishu.reflection.Reflection;
import timber.log.Timber;

public class AnywhereApplication extends Application {

    public static AnywhereRepository sRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new ThreadAwareDebugTree());
        } else {
            IzukoHelper.checkSignature();
            Timber.plant(new ReleaseTree());
        }

        GlobalValues.init(this);
        sRepository = new AnywhereRepository(this);
        Once.initialise(this);
        Settings.init();
    }

    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Reflection.unseal(base);
        ShizukuHelper.bind(base);
    }
}
