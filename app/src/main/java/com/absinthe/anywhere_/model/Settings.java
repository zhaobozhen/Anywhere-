package com.absinthe.anywhere_.model;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatDelegate;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.utils.IconPackManager;
import com.absinthe.anywhere_.utils.UiUtils;

public class Settings {
    @SuppressLint("StaticFieldLeak")
    public static IconPackManager mIconPackManager;

    public static void init() {
        setTheme(GlobalValues.sDarkMode);
        initIconPackManager();
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

    private static void initIconPackManager() {
        mIconPackManager = new IconPackManager();
        mIconPackManager.setContext(AnywhereApplication.sContext);
    }
}
