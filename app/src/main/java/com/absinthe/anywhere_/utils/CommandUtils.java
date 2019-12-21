package com.absinthe.anywhere_.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import moe.shizuku.api.RemoteProcess;
import moe.shizuku.api.ShizukuService;

public class CommandUtils {

    /**
     * execute adb command
     *
     * @param cmd command
     */
    public static String execAdbCmd(String cmd) {
        String result = null;

        switch (GlobalValues.sWorkingMode) {
            case Const.WORKING_MODE_SHIZUKU:
                result = execShizukuCmd(cmd);
                break;
            case Const.WORKING_MODE_ROOT:
                result = execRootCmd(cmd);
                break;
            case Const.WORKING_MODE_URL_SCHEME:
                ToastUtil.makeText(R.string.toast_change_work_mode);
                break;
        }
        Logger.d("execCmd result = ", result);
        return result;
    }

    /**
     * execute adb or intent command
     *
     * @param cmd command
     */
    public static String execCmd(String cmd) {
        String result = null;

        if (cmd.contains("am start -a") || !cmd.contains("am start")) {
            cmd = cmd.replace(Const.CMD_OPEN_URL_SCHEME, "");
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(cmd));
                AnywhereApplication.sContext.startActivity(intent);
                result = Intent.ACTION_VIEW;
            } catch (Exception e) {
                Logger.e("URL_SCHEME:Exception:", e.getMessage());
            }
        } else {
            String pkgClsString = cmd.split(" ")[3];
            String pkg = pkgClsString.split("/")[0];
            String cls = pkgClsString.split("/")[1];

            if (UiUtils.isActivityExported(AnywhereApplication.sContext, new ComponentName(pkg, cls))) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName(pkg, cls));
                    AnywhereApplication.sContext.startActivity(intent);
                    result = Intent.ACTION_VIEW;
                } catch (Exception e) {
                    Logger.d("WORKING_MODE_URL_SCHEME:Exception:", e.getMessage());
                }
            } else {
                switch (GlobalValues.sWorkingMode) {
                    case Const.WORKING_MODE_SHIZUKU:
                        if (PermissionUtils.checkShizukuOnWorking(AnywhereApplication.sContext)
                                && PermissionUtils.shizukuPermissionCheck(MainActivity.getInstance())) {
                            result = execShizukuCmd(cmd);
                        }
                        break;
                    case Const.WORKING_MODE_ROOT:
                        result = execRootCmd(cmd);
                        break;
                    case Const.WORKING_MODE_URL_SCHEME:
                        ToastUtil.makeText(R.string.toast_change_work_mode);
                        break;
                }
            }
        }

        Logger.d("execCmd result = ", result);
        if (TextUtils.isEmpty(result)) {
            ToastUtil.makeText(R.string.toast_check_perm);
        }
        return result;
    }

    /**
     * execute adb or intent command by root
     *
     * @param cmd command
     */
    private static String execRootCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        OutputStream os = null;
        InputStream is = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// Rooted device has su command
            os = p.getOutputStream();
            is = p.getInputStream();

            Logger.i(cmd);
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
    private static String execShizukuCmd(String cmd) {
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

            Logger.d("newProcess: " + remoteProcess);
            Logger.d("waitFor: " + remoteProcess.waitFor());
            Logger.d("output: " + sb);

            return sb.toString();
        } catch (Throwable tr) {
            Log.e(PermissionUtils.class.getSimpleName(), "newProcess", tr);
            return null;
        }
    }

}
