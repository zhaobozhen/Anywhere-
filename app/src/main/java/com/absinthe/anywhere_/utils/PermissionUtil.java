package com.absinthe.anywhere_.utils;

import android.content.Intent;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import moe.shizuku.api.RemoteProcess;
import moe.shizuku.api.ShizukuService;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";

    public static void goToMIUIPermissionManager() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("extra_pkgname", "com.absinthe.anywhere_");
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;

        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到 root 账户
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
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
        Class<?> c = null;
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String miui = (String) get.invoke(c, "ro.miui.ui.version.code");
            if (miui != null) {
                return !miui.isEmpty();
            }
            return false;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }
}
