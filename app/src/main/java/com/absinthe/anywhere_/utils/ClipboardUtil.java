package com.absinthe.anywhere_.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClipboardUtil {
    public interface Function {
        /**
         * Invokes the function.
         */
        void invoke(String text);
    }

    public static void getClipBoardText(@NonNull Activity activity, final Function f) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getTextFroClipFromAndroidQ(activity, f);
        } else {
            f.invoke(getTextFromClip(activity));
        }
    }

    public static void clearClipboard(Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", "");
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
        }
    }

    /**
     * Android Q get content from clipboard
     */
    @TargetApi(Build.VERSION_CODES.Q)
    private static void getTextFroClipFromAndroidQ(@NonNull final Activity activity, final Function f) {
        Runnable runnable = () -> {
            ClipboardManager clipboardManager =
                    (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (null == clipboardManager || !clipboardManager.hasPrimaryClip()) {
                f.invoke("");
                return;
            }
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (null == clipData || clipData.getItemCount() < 1) {
                f.invoke("");
                return;
            }
            ClipData.Item item = clipData.getItemAt(0);
            if (item == null) {
                f.invoke("");
                return;
            }
            CharSequence clipText = item.getText();
            if (TextUtils.isEmpty(clipText))
                f.invoke("");
            else
                f.invoke(clipText.toString());
        };

        activity.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activity.getWindow().getDecorView().removeCallbacks(runnable);
            }
        });
        activity.getWindow().getDecorView().post(runnable);
    }

    private static String getTextFromClip(Context context) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == clipboardManager || !clipboardManager.hasPrimaryClip()) {
            return "";
        }
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (null == clipData || clipData.getItemCount() < 1) {
            return "";
        }
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null)
            return "";
        CharSequence clipText = item.getText();
        if (TextUtils.isEmpty(clipText))
            return "";
        else
            return clipText.toString();

    }
}
