package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnAppUnfreezeListener;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.absinthe.anywhere_.utils.manager.ActivityStackManager;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import timber.log.Timber;

public class PermissionUtils {

    /**
     * Judge that whether device is miui
     */
    public static boolean isMIUI() {
        try {
            String brand = android.os.Build.BRAND.toLowerCase();
            Timber.d("brand = %s", brand);

            if (!brand.contains("xiaomi") && !brand.contains("redmi")) {
                return false;
            }

            @SuppressLint("PrivateApi")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String result = (String) get.invoke(c, "ro.miui.ui.version.code");

            return !TextUtils.isEmpty(result);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

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
            process = Runtime.getRuntime().exec("su"); //change to super user
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Timber.d("upgradeRootPermission: %s", e.toString());
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

    public static void unfreezeApp(Context context, String pkgName, OnAppUnfreezeListener listener) {
        try {
            if (IceBox.getAppEnabledSetting(context, pkgName) != 0) { //0 为未冻结状态
                if (ContextCompat.checkSelfPermission(context, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.isMIUI()) {
                        if (context instanceof ShortcutsActivity) {
                            context.startActivity(new Intent(context, MainActivity.class));
                            new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    DialogManager.showGrantPrivilegedPermDialog(ActivityStackManager.getInstance().getTopActivity()), 200);
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
