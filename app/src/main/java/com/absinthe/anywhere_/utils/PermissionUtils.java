package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.ShizukuHelper;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuClientHelperPre23;
import moe.shizuku.api.ShizukuService;

public class PermissionUtils {
    public static final int REQUEST_CODE_PERMISSION_V3 = 1001;
    public static final int REQUEST_CODE_AUTHORIZATION_V3 = 1002;

    /**
     * bump to miui permission management activity
     *
     * @param context to launch an activity
     */
    public static void goToMIUIPermissionManager(Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.makeText("打开失败，请自行前往 MIUI 权限界面");
        }

    }

    /**
     * acquire su permission
     *
     * @param pkgCodePath to get su permission of the package
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;

        try {
            String cmd = "chmod 777 " + pkgCodePath;
            Logger.d("root cmd =", cmd);
            process = Runtime.getRuntime().exec("su"); //change to super user
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Logger.d("upgradeRootPermission:", e.toString());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return process.waitFor() == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Judge that whether device is miui
     */
    public static boolean isMIUI() {
        try {
            String brand = android.os.Build.BRAND.toLowerCase();
            Logger.d("brand =", brand);

            if (!brand.contains("xiaomi") && !brand.contains("redmi")) {
                return false;
            }

            @SuppressLint("PrivateApi")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String result = (String) get.invoke(c, "ro.miui.ui.version.code");

            if (result != null) {
                return !result.isEmpty();
            }

            return false;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void showPermissionDialog(Fragment fragment) {
        Activity activity = fragment.getActivity();

        if (activity != null) {
            DialogManager.showGotoShizukuManagerDialog(activity, (dialog, which) -> {
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                if (intent != null) {
                    fragment.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                } else {
                    ToastUtil.makeText(R.string.toast_not_install_shizuku);
                    URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, fragment);
                }
            });
        }
    }

    /**
     * check shizuku permission
     *
     * @param activity to bind an activity to show a dialog
     */
    public static boolean shizukuPermissionCheck(Activity activity) {
        if (!ShizukuClientHelperPre23.isPreM()) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (ActivityCompat.checkSelfPermission(activity, ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.isMIUI()) {
                    DialogManager.showGotoShizukuManagerDialog(activity, (dialog, which) -> {
                        Intent intent = activity.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                        if (intent != null) {
                            activity.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                        } else {
                            ToastUtil.makeText(R.string.toast_not_install_shizuku);
                            URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, activity);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(activity), new String[]{ShizukuApiConstants.PERMISSION}, REQUEST_CODE_PERMISSION_V3);
                }
                return false;
            } else {
                return true;
            }
        } else if (!ShizukuHelper.isShizukuV3TokenValid()) {
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelperPre23.createPre23AuthorizationIntent(activity);
            if (intent != null) {
                try {
                    activity.startActivityForResult(intent, REQUEST_CODE_AUTHORIZATION_V3);
                    return true;
                } catch (Throwable tr) {
                    // should never happened
                    return false;
                }
            } else {
                // activity not found
                Logger.d("activity not found.");
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean shizukuPermissionCheck(Fragment fragment) {
        Activity activity = fragment.getActivity();

        if (activity != null) {
            if (!ShizukuClientHelperPre23.isPreM()) {
                // on API 23+, Shizuku v3 uses runtime permission
                if (ActivityCompat.checkSelfPermission(activity, ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.isMIUI()) {
                        showPermissionDialog(fragment);
                    } else {
                        fragment.requestPermissions(new String[]{ShizukuApiConstants.PERMISSION}, REQUEST_CODE_PERMISSION_V3);
                    }
                    return false;
                } else {
                    return true;
                }
            } else if (!ShizukuHelper.isShizukuV3TokenValid()) {
                // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
                Intent intent = ShizukuClientHelperPre23.createPre23AuthorizationIntent(activity);
                if (intent != null) {
                    try {
                        fragment.startActivityForResult(intent, REQUEST_CODE_AUTHORIZATION_V3);
                        return true;
                    } catch (Throwable tr) {
                        // should never happened
                        return false;
                    }
                } else {
                    // activity not found
                    Logger.d("activity not found.");
                    ToastUtil.makeText("activity not found.");
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * check overlay permission
     *
     * @param activity    to start an intent to permission activity
     * @param requestCode get result
     */
    public static boolean checkOverlayPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                try {
                    activity.startActivityForResult(
                            new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())),
                            requestCode);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                ToastUtil.makeText(R.string.toast_permission_overlap);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean checkOverlayPermission(Fragment fragment, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(fragment.getContext())) {
                try {
                    fragment.startActivityForResult(
                            new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + AnywhereApplication.sContext.getPackageName())),
                            requestCode);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                ToastUtil.makeText(R.string.toast_permission_overlap);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + AnywhereApplication.sContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                ToastUtil.makeText(R.string.toast_permission_overlap);
                return false;
            } else {
                return true;
            }
        }
        return true;
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
            Logger.d("Shizuku v3 may not running.");
            DialogManager.showCheckShizukuWorkingDialog(mContext);
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true;
        }
        return false;
    }

    public static void unfreezeApp(Context context, String pkgName, OnAppUnfreezeListener listener) {
        try {
            if (IceBox.getAppEnabledSetting(context, pkgName) != 0) { //0 为未冻结状态
                if (ContextCompat.checkSelfPermission(context, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.isMIUI()) {
                        if (context instanceof ShortcutsActivity) {
                            context.startActivity(new Intent(context, MainActivity.class));
                            new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    DialogManager.showGrantPrivilegedPermDialog(MainActivity.getInstance()), 200);
                        } else {
                            DialogManager.showGrantPrivilegedPermDialog((AppCompatActivity) context);
                        }
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{IceBox.SDK_PERMISSION}, 0x233);
                    }
                } else {
                    new Thread(() -> {
                        ((Activity) context).runOnUiThread(() -> ToastUtil.makeText(R.string.toast_defrosting));
                        IceBox.setAppEnabledSettings(context, true, pkgName);
                        ((Activity) context).runOnUiThread(listener::onAppUnfrozen);
                    }).start();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
