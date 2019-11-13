package com.absinthe.anywhere_.ui.shortcuts;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShortcutsActivity extends AppCompatActivity {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";
    public static final String ACTION_START_FROM_WIDGET = "START_FROM_WIDGET";

    @SuppressLint("WrongThread")
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
                    String result = PermissionUtil.execCmd(cmd);
                    if (result == null) {
                        ToastUtil.makeText(R.string.toast_check_perm);
                    }
                }
            } else if (action.equals(ACTION_START_FROM_WIDGET)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND);
                if (cmd != null) {
                    try {
                        String packageName = cmd.split(" ")[3].split("/")[0];
                        if (IceBox.getAppEnabledSetting(this, packageName) != 0) { //0 为未冻结状态
                            if (ContextCompat.checkSelfPermission(AnywhereApplication.sContext, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                                if (PermissionUtil.isMIUI()) {
                                    new MaterialAlertDialogBuilder(this)
                                            .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                                            .setPositiveButton(R.string.dialog_delete_positive_button, null)
                                            .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, in) -> {
                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                intent.setComponent(new ComponentName("com.android.settings",
                                                        "com.android.settings.Settings$ManageApplicationsActivity"));
                                                this.startActivity(intent);
                                            })
                                            .show();
                                } else {
                                    ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{IceBox.SDK_PERMISSION}, 0x233);
                                }
                            } else {
                                new Thread(() -> {
                                    IceBox.setAppEnabledSettings(ShortcutsActivity.this, true, packageName);
                                    String result = PermissionUtil.execCmd(cmd);
                                    if (result == null) {
                                        runOnUiThread(() -> ToastUtil.makeText(R.string.toast_check_perm));
                                    }
                                }).start();
                            }
                        } else {
                            String result = PermissionUtil.execCmd(cmd);
                            if (result == null) {
                                ToastUtil.makeText(R.string.toast_check_perm);
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        String result = PermissionUtil.execCmd(cmd);
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
