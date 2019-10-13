package com.absinthe.anywhere_.ui.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.utils.PermissionUtil;
import com.absinthe.anywhere_.utils.ToastUtil;

public class ShortcutsActivity extends Activity {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String action = i.getAction();

        if (action != null) {
            if (action.equals(ACTION_START_COLLECTOR)) {
                if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("shortcutEditUrl", "true");
                    startActivity(intent);
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
            }
        }
        finish();
    }
}
