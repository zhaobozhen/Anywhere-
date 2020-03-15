package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.FileUriExposedException;
import android.text.TextUtils;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.CommandResult;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.model.QREntity;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.ShizukuHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import moe.shizuku.api.RemoteProcess;
import moe.shizuku.api.ShizukuService;
import timber.log.Timber;

public class CommandUtils {

    /**
     * execute adb command
     *
     * @param cmd command
     */
    public static String execAdbCmd(String cmd) {
        String result = null;

        switch (GlobalValues.getWorkingMode()) {
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
        Timber.d("execCmd result = %s", result);
        return result;
    }

    /**
     * execute adb or intent command
     *
     * @param cmd command
     */
    @SuppressLint("NewApi")
    public static void execCmd(String cmd) {
        if (cmd == null) {
            return;
        }
        String result;

        if (cmd.startsWith("am start -a")) {
            cmd = cmd.replace(Const.CMD_OPEN_URL_SCHEME, "");
            try {
                URLSchemeHandler.parse(cmd, AnywhereApplication.sContext);
                result = CommandResult.RESULT_SUCCESS;
            } catch (ActivityNotFoundException e) {
                Timber.e(e);
                result = CommandResult.RESULT_NO_REACT_URL;
            } catch (FileUriExposedException e) {
                Timber.e(e);
                result = CommandResult.RESULT_FILE_URI_EXPOSED;
            }
        } else if (cmd.startsWith("am start -n")) {
            String pkgClsString = cmd.replace("am start -n ", "");
            String pkg = pkgClsString.split("/")[0];
            String cls = pkgClsString.split("/")[1];

            if (UiUtils.isActivityExported(AnywhereApplication.sContext, new ComponentName(pkg, cls))) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName(pkg, cls));
                    AnywhereApplication.sContext.startActivity(intent);
                    result = CommandResult.RESULT_SUCCESS;
                } catch (ActivityNotFoundException e) {
                    Timber.d(e);
                    result = CommandResult.RESULT_NO_REACT_URL;
                } catch (SecurityException e) {
                    Timber.d(e);
                    result = CommandResult.RESULT_SECURITY_EXCEPTION;
                }
            } else {
                result = execAdbCmd(cmd);
            }
        } else {
            if (cmd.startsWith(AnywhereType.QRCODE_PREFIX)) {
                cmd = cmd.replace(AnywhereType.QRCODE_PREFIX, "");
                QREntity entity = QRCollection.Singleton.INSTANCE.getInstance().getQREntity(cmd);
                if (entity != null) {
                    entity.launch();
                }
                result = CommandResult.RESULT_SUCCESS;
            } else if (cmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                cmd = cmd.replace(AnywhereType.SHELL_PREFIX, "");
                execAdbCmd(cmd);
                result = CommandResult.RESULT_SUCCESS;
            } else if (cmd.contains("://")) {
                try {
                    URLSchemeHandler.parse(cmd, AnywhereApplication.sContext);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_no_react_url);
                }
                result = CommandResult.RESULT_URL_SCHEME;
            } else {
                result = execAdbCmd(cmd);
            }
        }

        Timber.d("execCmd result = %s", result);
        if (!TextUtils.isEmpty(result)) {
            switch (result) {
                case CommandResult.RESULT_NO_REACT_URL:
                    ToastUtil.makeText(R.string.toast_no_react_url);
                    break;
                case CommandResult.RESULT_ROOT_PERM_ERROR:
                    ToastUtil.makeText(R.string.toast_check_perm);
                    break;
                case CommandResult.RESULT_SHIZUKU_PERM_ERROR:
                    ToastUtil.makeText(R.string.toast_check_perm);
                    try {
                        ShizukuHelper.requestShizukuPermission();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommandResult.RESULT_FILE_URI_EXPOSED:
                    ToastUtil.makeText(R.string.toast_file_uri_exposed);
                    break;
                case CommandResult.RESULT_SECURITY_EXCEPTION:
                    ToastUtil.makeText(R.string.toast_security_exception);
                    break;
                default:
            }
        }
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

            Timber.i(cmd);
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
            result.append(CommandResult.RESULT_ROOT_PERM_ERROR);
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
        if (result.toString().isEmpty()) {
            result.append(CommandResult.RESULT_ROOT_PERM_ERROR);
        }
        return result.toString();
    }

    /**
     * execute adb or intent via shizuku manager
     *
     * @param cmd command
     */
    private static String execShizukuCmd(String cmd) {
        Timber.d(cmd);
        StringBuilder sb = new StringBuilder();
        try {
            RemoteProcess remoteProcess = ShizukuService.newProcess(new String[]{"sh"}, null, null);
            InputStream is = remoteProcess.getInputStream();
            OutputStream os = remoteProcess.getOutputStream();
            os.write((cmd + "\n").getBytes());
            os.write("exit\n".getBytes());
            os.close();

            int c;
            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
            is.close();

            Timber.d("newProcess: %s", remoteProcess);
            Timber.d("waitFor: %s", remoteProcess.waitFor());
            Timber.d("output: %s", sb);
        } catch (Throwable tr) {
            Timber.e(tr, "newProcess");
            sb.append(CommandResult.RESULT_SHIZUKU_PERM_ERROR);
        }
        return sb.toString();
    }

}
