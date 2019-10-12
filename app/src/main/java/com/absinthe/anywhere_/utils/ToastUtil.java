package com.absinthe.anywhere_.utils;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.absinthe.anywhere_.ui.main.MainActivity;

public class ToastUtil {
    public static void makeText(String text) {
        Toast.makeText(MainActivity.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public static void makeText(@StringRes int resId) {
        Toast.makeText(MainActivity.getInstance(), MainActivity.getInstance().getText(resId), Toast.LENGTH_SHORT).show();
    }
}
