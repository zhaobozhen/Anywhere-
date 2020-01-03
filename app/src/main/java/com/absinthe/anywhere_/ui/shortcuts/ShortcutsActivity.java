package com.absinthe.anywhere_.ui.shortcuts;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.model.QREntity;
import com.absinthe.anywhere_.services.CollectorService;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CommandUtils;
import com.absinthe.anywhere_.utils.Logger;
import com.absinthe.anywhere_.utils.PermissionUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.catchingnow.icebox.sdk_client.IceBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class ShortcutsActivity extends AppCompatActivity implements LifecycleOwner {
    public static final String ACTION_START_COLLECTOR = "START_COLLECTOR";
    public static final String ACTION_START_COMMAND = "START_COMMAND";
    public static final String ACTION_START_FROM_WIDGET = "START_FROM_WIDGET";
    public static final String ACTION_START_QR_CODE = "START_QR_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnywhereViewModel viewModel = ViewModelProviders.of(this).get(AnywhereViewModel.class);
        Intent i = getIntent();
        String action = i.getAction();

        if (action != null) {
            if (action.equals(ACTION_START_COLLECTOR)) {
                if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                    AppUtils.openUrl(this, "", "", "");
                } else {
                    if (PermissionUtils.checkOverlayPermission(this, Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION)) {
                        Intent intent = new Intent(this, CollectorService.class);
                        intent.putExtra(CollectorService.COMMAND, CollectorService.COMMAND_OPEN);
                        startService(intent);
                    }
                }
                finish();
            } else if (action.equals(ACTION_START_COMMAND)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                if (cmd != null) {
                    String packageName = TextUtils.getPkgNameByCommand(cmd);
                    try {
                        if (IceBox.getAppEnabledSetting(this, packageName) != 0) {
                            PermissionUtils.unfreezeApp(this, packageName, () ->
                                    CommandUtils.execCmd(cmd));
                        } else {
                            CommandUtils.execCmd(cmd);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        CommandUtils.execCmd(cmd);
                    }
                }
                finish();
            } else if (action.equals(ACTION_START_FROM_WIDGET)) {
                String cmd = i.getStringExtra(Const.INTENT_EXTRA_WIDGET_COMMAND);
                if (cmd != null) {
                    try {
                        String packageName = TextUtils.getPkgNameByCommand(cmd);
                        if (IceBox.getAppEnabledSetting(this, packageName) != 0) { //0 为未冻结状态
                            PermissionUtils.unfreezeApp(this, packageName, () ->
                                    CommandUtils.execCmd(cmd));
                        } else {
                            CommandUtils.execCmd(cmd);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        CommandUtils.execCmd(cmd);
                    } catch (IndexOutOfBoundsException e2) {
                        e2.printStackTrace();
                        ToastUtil.makeText(R.string.toast_wrong_cmd);
                    }
                }
                finish();
            } else if (action.equals(ACTION_START_QR_CODE)) {
                String id = i.getStringExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD);
                if (id != null) {
                    id = id.replace(QREntity.PREFIX, "");
                    QREntity entity = QRCollection.Singleton.INSTANCE.getInstance().getQREntity(id);
                    if (entity != null) {
                        entity.launch();
                    }
                }
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
                    new MaterialAlertDialogBuilder(ShortcutsActivity.this, R.style.AppTheme_Dialog)
                            .setAdapter(arrayAdapter, (dialogInterface, i1) -> {
                                Intent shortcutIntent = new Intent(ShortcutsActivity.this, ShortcutsActivity.class);
                                String cmd = TextUtils.getItemCommand(
                                        Objects.requireNonNull(anywhereEntities).get(i1));
                                if (cmd.startsWith(QREntity.PREFIX)) {
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
            } else if (action.equals(Intent.ACTION_VIEW)) {
                Uri uri = i.getData();
                if (uri != null) {
                    String host = uri.getHost();

                    if (android.text.TextUtils.equals(host, Const.HOST_OPEN)) {
                        String param1 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_1);
                        String param2 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_2);
                        String param3 = uri.getQueryParameter(Const.INTENT_EXTRA_PARAM_3);

                        if (param1 != null && param2 != null && param3 != null) {
                            if (param2.isEmpty() && param3.isEmpty()) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse(param1));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                AnywhereEntity ae = AnywhereEntity.Builder()
                                        .setParam1(param1)
                                        .setParam2(param2)
                                        .setParam3(param3)
                                        .setType(AnywhereType.ACTIVITY);

                                CommandUtils.execCmd(TextUtils.getItemCommand(ae));
                            }
                        }
                    }
                }
                finish();
            }
        }
    }
}
