package com.absinthe.anywhere_.ui.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.catchingnow.icebox.sdk_client.IceBox;

public class ShortcutsActivity extends Activity {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";
    public static final String ACTION_START_FROM_WIDGET = "START_FROM_WIDGET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String action = i.getAction();

        if (action != null) {
            if (action.equals(ACTION_START_COLLECTOR)) {
                if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                    AppUtils.openUrl(this, "", "", "");
                } else {
                    if (PermissionUtil.checkOverlayPermission(this, Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        Intent intent = new Intent(this, CollectorService.class);
                        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
                        startService(intent);
                    }
                }
            } else if (action.equals(ACTION_START_COMMAND)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                if (cmd != null) {
                    String packageName = TextUtils.getPkgNameByCommand(cmd);
                    try {
                        if (IceBox.getAppEnabledSetting(this, packageName) != 0) {
                            PermissionUtil.unfreezeApp(this, packageName, () -> {
                                String result = CommandUtils.execCmd(cmd);
                                if (result == null) {
                                    ToastUtil.makeText(R.string.toast_check_perm);
                                }
                            });
                        } else {
                            String result = CommandUtils.execCmd(cmd);
                            if (result == null) {
                                ToastUtil.makeText(R.string.toast_check_perm);
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        String result = CommandUtils.execCmd(cmd);
                        if (result == null) {
                            ToastUtil.makeText(R.string.toast_check_perm);
                        }
                    }
                }
            } else if (action.equals(ACTION_START_FROM_WIDGET)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND);
                if (cmd != null) {
                    try {
                        String packageName = TextUtils.getPkgNameByCommand(cmd);
                        if (IceBox.getAppEnabledSetting(this, packageName) != 0) { //0 为未冻结状态
                            PermissionUtil.unfreezeApp(this, packageName, () -> {
                                String result = CommandUtils.execCmd(cmd);
                                if (result == null) {
                                    ToastUtil.makeText(R.string.toast_check_perm);
                                }
                            });
                        } else {
                            String result = CommandUtils.execCmd(cmd);
                            if (result == null) {
                                ToastUtil.makeText(R.string.toast_check_perm);
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        String result = CommandUtils.execCmd(cmd);
                        if (result == null) {
                            ToastUtil.makeText(R.string.toast_check_perm);
                        }
                    } catch (IndexOutOfBoundsException e2) {
                        e2.printStackTrace();
                        ToastUtil.makeText(R.string.toast_wrong_cmd);
                    }
                }
            }
        }
        finish();
    }
}
