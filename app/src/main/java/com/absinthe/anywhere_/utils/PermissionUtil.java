package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import moe.shizuku.api.RemoteProcess;
import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.api.ShizukuClientHelper;
import moe.shizuku.api.ShizukuService;

public class PermissionUtil {
    private static final Class klass = PermissionUtil.class;
    private static final int REQUEST_CODE_PERMISSION_V3 = 1001;
    private static final int REQUEST_CODE_AUTHORIZATION_V3 = 1002;

    /**
     * bump to miui permission management activity
     *
     * @param context to launch an activity
     */
    public static void goToMIUIPermissionManager(Context context) {
        try {
            Intent intent = new Intent();
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
            LogUtil.d(klass, "root cmd =", cmd);
            process = Runtime.getRuntime().exec("su"); //切换到 root 账户
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            LogUtil.d(klass, "upgradeRootPermission:", e.toString());
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
     * execute adb or intent command
     *
     * @param cmd command
     */
    public static String execCmd(String cmd) {
        String result = null;

        switch (GlobalValues.sWorkingMode) {
            case Const.WORKING_MODE_SHIZUKU:
                result = execShizukuCmd(cmd);
                break;
            case Const.WORKING_MODE_ROOT:
                result = execRootCmd(cmd);
                break;
            case Const.WORKING_MODE_URL_SCHEME:
                if (cmd.contains("am start -n")) {
                    try {
                        String pkgClsString = cmd.split(" ")[3];
                        String pkg = pkgClsString.split("/")[0];
                        String cls = pkgClsString.split("/")[1];
                        if (cls.charAt(0) == '.') {
                            cls = pkg + cls;
                        }
                        if (!UiUtils.isActivityExported(AnywhereApplication.sContext, new ComponentName(pkg, cls))) {
                            ToastUtil.makeText(R.string.toast_change_work_mode);
                            break;
                        } else {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setComponent(new ComponentName(pkg, cls));
                                MainActivity.getInstance().startActivity(intent);
                                result = "android.intent.action.VIEW";
                            } catch (Exception e) {
                                LogUtil.d(klass, "WORKING_MODE_URL_SCHEME:Exception:", e.getMessage());
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        ToastUtil.makeText(R.string.toast_wrong_cmd);
                    }
                } else {
                    if (cmd.contains(Const.CMD_OPEN_URL_SCHEME)) {
                        cmd = cmd.replace(Const.CMD_OPEN_URL_SCHEME, "");
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(cmd));
                        MainActivity.getInstance().startActivity(intent);
                        result = "android.intent.action.VIEW";
                    } catch (Exception e) {
                        LogUtil.d(klass, "WORKING_MODE_URL_SCHEME:Exception:", e.getMessage());
                    }
                }
                break;
        }
        LogUtil.d(klass, "execCmd result = ", result);
        return result;
    }

    /**
     * execute adb or intent command by root
     *
     * @param cmd command
     */
    public static String execRootCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        OutputStream os = null;
        InputStream is = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过 Root 处理的 android 系统即有 su 命令
            os = p.getOutputStream();
            is = p.getInputStream();

            LogUtil.i(klass, cmd);
            os.write((cmd + "\n").getBytes());
            os.flush();
            os.write("exit\n".getBytes());
            os.flush();

            int c;
            while ((c = is.read()) != -1) {
                result.append((char) c);
            }

            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * execute adb or intent via shizuku manager
     *
     * @param cmd command
     */
    public static String execShizukuCmd(String cmd) {
        try {
            RemoteProcess remoteProcess = ShizukuService.newProcess(new String[]{"sh"}, null, null);
            InputStream is = remoteProcess.getInputStream();
            OutputStream os = remoteProcess.getOutputStream();
            os.write((cmd + "\n").getBytes());
            os.write("exit\n".getBytes());
            os.close();

            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
            is.close();

            LogUtil.d(klass, "newProcess: " + remoteProcess);
            LogUtil.d(klass, "waitFor: " + remoteProcess.waitFor());
            LogUtil.d(klass, "output: " + sb);

            return sb.toString();
        } catch (Throwable tr) {
            Log.e(klass.getSimpleName(), "newProcess", tr);
            return null;
        }
    }

    /**
     * Judge that whether device is miui
     */
    public static boolean isMIUI() {
        try {
            String brand = android.os.Build.BRAND.toLowerCase();
            LogUtil.d(klass, "brand =", brand);
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

    /**
     * show permission dialog
     *
     * @param activity to bind an activity to show
     */
    private static void showPermissionDialog(Activity activity) {
        new MaterialAlertDialogBuilder(activity, R.style.AppTheme_Dialog)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                    if (intent != null) {
                        activity.startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                    } else {
                        ToastUtil.makeText(R.string.toast_not_install_shizuku);
                        intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse("https://www.coolapk.com/moe.shizuku.privileged.api"));
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    /**
     * check shizuku permission
     *
     * @param activity to bind an activity to show a dialog
     */
    public static boolean shizukuPermissionCheck(Activity activity) {
        if (!ShizukuClientHelper.isPreM()) {
            // on API 23+, Shizuku v3 uses runtime permission
            if (ActivityCompat.checkSelfPermission(activity, ShizukuApiConstants.PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtil.isMIUI()) {
                    showPermissionDialog(activity);
                } else {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(activity), new String[]{ShizukuApiConstants.PERMISSION}, REQUEST_CODE_PERMISSION_V3);
                }
                return false;
            } else {
                return true;
            }
        } else if (!AnywhereApplication.isShizukuV3TokenValid()) {
            // on API pre-23, Shizuku v3 uses old token, get token from Shizuku app
            Intent intent = ShizukuClientHelper.createPre23AuthorizationIntent(activity);
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
                LogUtil.d(klass, "activity not found.");
                ToastUtil.makeText("activity not found.");
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
                } catch (NullPointerException e) {
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
            if (AnywhereApplication.isShizukuV3Failed()) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                ToastUtil.makeText("provider started with no binder included.");
            }

            // Shizuku v3 may not running, notify user
            LogUtil.d(klass, "Shizuku v3 may not running.");
            new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                    .setMessage(R.string.dialog_message_shizuku_not_running)
                    .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                        if (intent != null) {
                            ((AppCompatActivity) mContext).startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                        } else {
                            ToastUtil.makeText(R.string.toast_not_install_shizuku);
                            intent = new Intent("android.intent.action.VIEW");
                            intent.setData(Uri.parse("https://www.coolapk.com/moe.shizuku.privileged.api"));
                            mContext.startActivity(intent);
                        }
                    })
                    .show();
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
                    if (PermissionUtil.isMIUI()) {
                        new MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                                .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, in) -> {
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    intent.setComponent(new ComponentName("com.android.settings",
                                            "com.android.settings.Settings$ManageApplicationsActivity"));
                                    context.startActivity(intent);
                                })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
                    }
                } else {
                    new Thread(() -> {
                        ((Activity)context).runOnUiThread(() -> ToastUtil.makeText(R.string.toast_defrosting));
                        IceBox.setAppEnabledSettings(context, true, pkgName);
                        ((Activity)context).runOnUiThread(listener::onAppUnfrozen);
                    }).start();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
