package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtil {
    private static Vibrator vibrator;

    public static final int HEAVY_CLICK = VibrationEffect.EFFECT_HEAVY_CLICK;
    @SuppressLint("InlinedApi")
    public static final int DEFAULT = VibrationEffect.DEFAULT_AMPLITUDE;

    public static void vibrate(Context context, int effect) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        LogUtil.d(VibratorUtil.class, "vibrator =", vibrator);
        if (vibrator != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(effect);
                } else {
                    vibrator.vibrate(20);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
