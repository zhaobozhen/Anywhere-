package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
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
    private static final String TAG = "PermissionUtil";
    private static final int REQUEST_CODE_PERMISSION_V3 = 1001;
    private static final int REQUEST_CODE_AUTHORIZATION_V3 = 1002;

    public static void goToMIUIPermissionManager(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        context.startActivity(intent);
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;

        try {
            String cmd = "chmod 777 " + pkgCodePath;
            Log.d(TAG, "root cmd = " + cmd);
            process = Runtime.getRuntime().exec("su"); //切换到 root 账户
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d(TAG, "upgradeRootPermission:" + e.toString());
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

    public static String execCmd(String cmd) {
        String result = null;
        if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_ROOT)) {
            result = execRootCmd(cmd);
        } else if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_SHIZUKU)) {
            result = execShizukuCmd(cmd);
        } else {
            Log.d(TAG, "execCmd abnormal.");
        }

        return result;
    }

    public static String execRootCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        OutputStream os = null;
        InputStream is = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过 Root 处理的 android 系统即有 su 命令
            os = p.getOutputStream();
            is = p.getInputStream();

            Log.i(TAG, cmd);
            os.write((cmd + "\n").getBytes());
            os.flush();
            os.write("exit\n".getBytes());
            os.flush();

            int c;
            while ((c = is.read()) != -1) {
                result.append((char)c);
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

            Log.d(TAG, "newProcess: " + remoteProcess);
            Log.d(TAG, "waitFor: " + remoteProcess.waitFor());
            Log.d(TAG, "output: " + sb);

            return sb.toString();
        } catch (Throwable tr) {
            Log.e(TAG, "newProcess", tr);
            return null;
        }
    }

    public static boolean isMIUI() {
        try {
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

    public static void showPermissionDialog(Context mContext) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                    if (intent != null) {
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Not install Shizuku.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> { })
                .show();
    }

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
        } else if (!AnywhereApplication.isShizukuV3TokenValid()){
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
                Log.d(TAG, "activity not found.");
                Toast.makeText(activity, "activity not found.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean checkOverlayPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                activity.startActivityForResult(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())),
                        requestCode
                );
                Toast.makeText(activity, R.string.toast_permission_overlap, Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean checkShizukuOnWorking(Context mContext) {
        // Shizuku v3 service will send binder via Content Provider to this process when this activity comes foreground.

        // Wait a few seconds here for binder

        if (!ShizukuService.pingBinder()) {
            if (AnywhereApplication.isShizukuV3Failed()) {
                // provider started with no binder included, binder calls blocked by SELinux or server dead, should never happened
                // notify user
                Toast.makeText(mContext, "provider started with no binder included.", Toast.LENGTH_SHORT).show();
            }

            // Shizuku v3 may not running, notify user
            Toast.makeText(mContext, "Shizuku v3 may not running.", Toast.LENGTH_SHORT).show();
            // if your app support Shizuku v2, run old v2 codes here
            // for new apps, recommended to ignore v2
        } else {
            // Shizuku v3 binder received
            return true;
        }
        return false;
    }
}
