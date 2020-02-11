package com.absinthe.anywhere_.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuMultiProcessHelper;
import moe.shizuku.api.ShizukuService;

import static com.absinthe.anywhere_.AnywhereApplication.getProcessName;

/**
 * Shizuku Helper
 * <p>
 * Init Shizuku API.
 */
public class ShizukuHelper {
    private static final String ACTION_SEND_BINDER = "moe.shizuku.client.intent.action.SEND_BINDER";
    private static boolean v3Failed;
    private static boolean v3TokenValid;

    public static boolean isShizukuV3Failed() {
        return v3Failed;
    }

    public static boolean isShizukuV3TokenValid() {
        return v3TokenValid;
    }

    public static void setShizukuV3TokenValid(boolean valid) {
        v3TokenValid = valid;
    }

    public static void bind(Context context) {
        Logger.d("initialize ", ShizukuMultiProcessHelper.initialize(context, !getProcessName().endsWith(":test")));

        ShizukuClientHelper.setBinderReceivedListener(() -> {
            Logger.d("onBinderReceived");

            if (ShizukuService.getBinder() == null) {
                // ShizukuBinderReceiveProvider started without binder, should never happened
                Logger.d("binder is null");
                v3Failed = true;
            } else {
                try {
                    // test the binder first
                    ShizukuService.pingBinder();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        String token = ShizukuClientHelper.loadPre23Token(context);
                        v3TokenValid = ShizukuService.setCurrentProcessTokenPre23(token);
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_SEND_BINDER));
                } catch (Throwable tr) {
                    // blocked by SELinux or server dead, should never happened
                    Log.i(context.getClass().getSimpleName(), "can't contact with remote", tr);
                    v3Failed = true;
                }
            }
        });
    }
}
