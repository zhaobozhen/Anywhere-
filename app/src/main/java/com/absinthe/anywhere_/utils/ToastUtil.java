package com.absinthe.anywhere_.utils;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.blankj.utilcode.util.Utils;

import me.drakeet.support.toast.ToastCompat;

public class ToastUtil {
    /**
     * make a toast via a string
     *
     * @param text a string text
     */
    public static void makeText(String text) {
        ToastCompat.makeText(Utils.getApp(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * make a toast via a resource id
     *
     * @param resId a string resource id
     */
    public static void makeText(@StringRes int resId) {
        ToastCompat.makeText(Utils.getApp(), Utils.getApp().getText(resId), Toast.LENGTH_SHORT).show();
    }
}
