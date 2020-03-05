package com.absinthe.anywhere_.ui.shortcuts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.handler.Opener;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;

import java.util.Objects;

public class ShortcutsActivity extends AppCompatActivity {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";
    public static final String ACTION_START_FROM_WIDGET = "START_FROM_WIDGET";
    public static final String ACTION_START_QR_CODE = "START_QR_CODE";
    public static final String ACTION_START_IMAGE = "START_IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiUtils.setActionBarTransparent(this);
        AnywhereViewModel viewModel = new ViewModelProvider(this).get(AnywhereViewModel.class);
        Intent i = getIntent();
        String action = i.getAction();

        if (action != null) {
            if (action.equals(ACTION_START_COLLECTOR)) {
                if (GlobalValues.getWorkingMode().equals(Const.WORKING_MODE_URL_SCHEME)) {
                    AppUtils.openNewURLScheme(this);
                } else {
                    if (PermissionUtils.checkOverlayPermission(this, Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        CollectorService.startCollector(this);
                    }
                }
                finish();
            } else if (action.equals(ACTION_START_COMMAND)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                if (cmd != null) {
                    if (cmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX) ||
                            cmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                        Opener.with(this)
                                .load(cmd)
                                .setOpenedListener(this::finish)
                                .open();
                    } else {
                        Opener.with(this).load(cmd).open();
                        finish();
                    }
                } else {
                    finish();
                }
            } else if (action.equals(ACTION_START_FROM_WIDGET)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND);
                if (cmd != null) {
                    if (cmd.startsWith(AnywhereType.DYNAMIC_PARAMS_PREFIX) ||
                            cmd.startsWith(AnywhereType.SHELL_PREFIX)) {
                        Opener.with(this)
                                .load(cmd)
                                .setOpenedListener(this::finish)
                                .open();
                    } else {
                        Opener.with(this).load(cmd).open();
                        finish();
                    }
                } else {
                    finish();
                }
            } else if (action.equals(ACTION_START_QR_CODE)) {
                String id = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                CommandUtils.execCmd(id);
                finish();
            } else if (action.equals(Intent.ACTION_CREATE_SHORTCUT)) {
                viewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AnywhereApplication.sContext, android.R.layout.select_dialog_singlechoice);
                    Logger.d("list=", anywhereEntities);
                    if (anywhereEntities != null) {
                        for (AnywhereEntity ae : anywhereEntities) {
                            arrayAdapter.add(ae.getAppName());
                        }
                    }
                    new AnywhereDialogBuilder(this)
                            .setAdapter(arrayAdapter, (dialogInterface, i1) -> {
                                Intent shortcutIntent = new Intent(ShortcutsActivity.this, ShortcutsActivity.class);
                                String cmd = TextUtils.getItemCommand(
                                        Objects.requireNonNull(anywhereEntities).get(i1));
                                if (cmd.startsWith(AnywhereType.QRCODE_PREFIX)) {
                                    shortcutIntent.setAction(ACTION_START_QR_CODE);
                                } else {
                                    shortcutIntent.setAction(ACTION_START_COMMAND);
                                }
                                shortcutIntent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, cmd);

                                Intent intent = new Intent();
                                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_open));
                                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(ShortcutsActivity.this, R.drawable.ic_shortcut_start_collector));
                                setResult(RESULT_OK, intent);
                                finish();
                            })
                            .setOnCancelListener(dialog -> finish())
                            .show();
                });
            } else if (action.equals(ACTION_START_IMAGE)) {
                String uri = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                if (uri != null) {
                    AnywhereEntity ae = AnywhereEntity.Builder();
                    ae.setParam1(uri);
                    DialogManager.showImageDialog(this, ae, this::finish);
                } else {
                    finish();
                }
            } else if (action.equals(Intent.ACTION_VIEW)) {
                Uri uri = i.getData();
                if (uri != null) {
                    String host = uri.getHost();

                    if (android.text.TextUtils.equals(host, URLManager.OPEN_HOST)) {
                        String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
                        String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
                        String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);

                        if (param1 != null && param2 != null && param3 != null) {
                            if (param2.isEmpty() && param3.isEmpty()) {
                                try {
                                    URLSchemeHandler.parse(param1, this);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                AnywhereEntity ae = AnywhereEntity.Builder();
                                ae.setParam1(param1);
                                ae.setParam2(param2);
                                ae.setParam3(param3);
                                ae.setType(AnywhereType.ACTIVITY);

                                CommandUtils.execCmd(TextUtils.getItemCommand(ae));
                            }
                        }
                    }
                }
                finish();
            }
        } else {
            finish();
        }
    }
}
