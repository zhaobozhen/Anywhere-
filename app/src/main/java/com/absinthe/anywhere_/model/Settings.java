package com.absinthe.anywhere_.model;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatDelegate;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.utils.IconPackManager;
import com.absinthe.anywhere_.utils.LogUtil;
import com.absinthe.anywhere_.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Settings {
    @SuppressLint("StaticFieldLeak")
    public static IconPackManager sIconPackManager;
    public static IconPackManager.IconPack sIconPack;
    public static String sDate;

    public static final String DEFAULT_ICON_PACK = "default.icon.pack";

    public static void init() {
        setLogger();
        setTheme(GlobalValues.sDarkMode);
        initIconPackManager();
        setDate();
        initQRCollection();
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
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "auto":
                AppCompatDelegate.setDefaultNightMode(UiUtils.getAutoDarkMode());
                break;
            default:
        }
    }

    public static void setLogger() {
        LogUtil.setDebugMode(BuildConfig.DEBUG | GlobalValues.sIsDebugMode);
    }

    private static void initIconPackManager() {
        sIconPackManager = new IconPackManager();
        sIconPackManager.setContext(AnywhereApplication.sContext);

        HashMap<String, IconPackManager.IconPack> hashMap = sIconPackManager.getAvailableIconPacks(true);

        for (Map.Entry<String, IconPackManager.IconPack> entry : hashMap.entrySet()) {
            if (entry.getKey().equals(GlobalValues.sIconPack)) {
                sIconPack = entry.getValue();
            }
        }
        if (sIconPack == null) {
            GlobalValues.setsIconPack(DEFAULT_ICON_PACK);
        }
    }

    private static void setDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        sDate = dateFormat.format(date);
    }

    private static void initQRCollection() {
        QRCollection.Singleton.INSTANCE.getInstance().setContext(AnywhereApplication.sContext);
    }
}
