package com.absinthe.anywhere_.utils.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.blankj.utilcode.util.IntentUtils;

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuClientHelperPre23;
import moe.shizuku.api.ShizukuMultiProcessHelper;
import moe.shizuku.api.ShizukuService;
import timber.log.Timber;

import static com.absinthe.anywhere_.AnywhereApplication.getProcessName;

/**
 * Shizuku Helper
 * <p>
 * Init Shizuku API.
 */
public class ShizukuHelper {
    private static final String ACTION_SEND_BINDER = "moe.shizuku.client.intent.action.SEND_BINDER";
    public static final int REQUEST_CODE_PERMISSION_V3 = 1001;
    public static final int REQUEST_CODE_AUTHORIZATION_V3 = 1002;
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
        Timber.d("initialize %s", ShizukuMultiProcessHelper.initialize(context, !getProcessName().endsWith(":test")));

        ShizukuClientHelper.setBinderReceivedListener(() -> {
            Timber.d("onBinderReceived");

            if (ShizukuService.getBinder() == null) {
                // ShizukuBinderReceiveProvider started without binder, should never happened
                Timber.d("binder is null");
                v3Failed = true;
            } else {
                try {
                    // test the binder first
                    ShizukuService.pingBinder();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        String token = ShizukuClientHelperPre23.loadPre23Token(context);
                        setShizukuV3TokenValid(ShizukuService.setTokenPre23(token));
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_SEND_BINDER));
                } catch (Throwable tr) {
                    // blocked by SELinux or server dead, should never happened
                    Timber.i(tr, "can't contact with remote");
                    v3Failed = true;
                }
            }
        });
    }

    /**
     * check whether shizuku service is on working
     *
     * @param mContext to show a dialog
     */
    public static boolean checkShizukuOnWorking(Context mContext) {
        // Shizuku v3 service will send binder via Content Provider to this process when this activity comes foreground.

        // Wait a few seconds here for binder

        if (!ShizukuService.pingBinder()) {
            if (ShizukuHelper.isShizukuV3Failed()) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                ToastUtil.makeText("provider started with no binder included.");
            }

            // Shizuku v3 may not running, notify user
            Timber.d("Shizuku v3 may not running.");
            DialogManager.showCheckShizukuWorkingDialog(mContext);
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true;
        }
        return false;
    }

    public static boolean isGrantShizukuPermission() {
        return com.blankj.utilcode.util.PermissionUtils.isGranted(ShizukuApiConstants.PERMISSION);
    }

    public static void requestShizukuPermission() {
        if (isGrantShizukuPermission()) {
            return;
        }

        Activity activity = ActivityStackManager.getInstance().getTopActivity();
        if (activity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (!isGrantShizukuPermission()) {
                if (PermissionUtils.isMIUI()) {
                    showPermissionDialog(activity);
                } else {
                    activity.requestPermissions(new String[]{ShizukuApiConstants.PERMISSION}, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                }
            }
        } else if (!ShizukuHelper.isShizukuV3TokenValid()) {
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelperPre23.createPre23AuthorizationIntent(activity);
            if (intent != null) {
                try {
                    activity.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                } catch (Throwable tr) {
                    // should never happened
                }
            } else {
                // activity not found
                Timber.d("activity not found.");
                ToastUtil.makeText("activity not found.");
            }
        }
    }

    public static void requestShizukuPermission(Fragment fragment) {
        if (isGrantShizukuPermission() || fragment == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (!isGrantShizukuPermission()) {
                if (PermissionUtils.isMIUI()) {
                    showPermissionDialog(fragment);
                } else {
                    fragment.requestPermissions(new String[]{ShizukuApiConstants.PERMISSION}, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                }
            }
        } else if (!ShizukuHelper.isShizukuV3TokenValid()) {
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelperPre23.createPre23AuthorizationIntent(ActivityStackManager.getInstance().getTopActivity());
            if (intent != null) {
                try {
                    fragment.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                } catch (Throwable tr) {
                    // should never happened
                }
            } else {
                // activity not found
                Timber.d("activity not found.");
                ToastUtil.makeText("activity not found.");
            }
        }
    }

    private static void showPermissionDialog(Activity activity) {
        if (activity != null) {
            DialogManager.showGotoShizukuManagerDialog(activity, (dialog, which) -> {
                Intent intent = IntentUtils.getLaunchAppIntent("moe.shizuku.privileged.api");
                if (intent != null) {
                    activity.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                } else {
                    ToastUtil.makeText(R.string.toast_not_install_shizuku);
                    URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, activity);
                }
            });
        }
    }

    private static void showPermissionDialog(Fragment fragment) {
        if (fragment != null) {
            DialogManager.showGotoShizukuManagerDialog(ActivityStackManager.getInstance().getTopActivity(), (dialog, which) -> {
                Intent intent = IntentUtils.getLaunchAppIntent("moe.shizuku.privileged.api");

                if (intent != null) {
                    fragment.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                } else {
                    ToastUtil.makeText(R.string.toast_not_install_shizuku);
                    URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, fragment);
                }
            });
        }
    }
}
