package com.absinthe.anywhere_.utils.manager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.ui.backup.RestoreApplyFragmentDialog;
import com.absinthe.anywhere_.ui.list.CardListDialogFragment;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.RenameFragmentDialog;
import com.absinthe.anywhere_.ui.settings.IconPackDialogFragment;
import com.absinthe.anywhere_.ui.settings.IntervalDialogFragment;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.ui.settings.TimePickerDialogFragment;
import com.absinthe.anywhere_.ui.shortcuts.CreateShortcutDialogFragment;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.ColorPickerDialogBuilder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;

import java.util.List;

/**
 * Dialog Manager
 * <p>
 * To manage all Dialogs / DialogFragments / BottomSheetDialogs in App.
 */
public class DialogManager {
    public static void showResetBackgroundDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_reset_background_confirm_title)
                .setMessage(R.string.dialog_reset_background_confirm_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    GlobalValues.setsBackgroundUri("");
                    MainActivity.getInstance().restartActivity();
                    if (SettingsActivity.getInstance() != null) {
                        SettingsActivity.getInstance().finish();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showClearShortcutsDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_reset_background_confirm_title)
                .setMessage(R.string.dialog_reset_shortcuts_confirm_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtils.clearShortcuts();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showBackupShareDialog(Context context, String dig, String encrypted) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.settings_backup_share_title)
                .setMessage(dig)
                .setPositiveButton(R.string.btn_backup_copy, (dialog, which) -> {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", encrypted);
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.makeText(R.string.toast_copied);
                    }
                })
                .setNeutralButton(R.string.btn_backup_share, (dialog, which) -> {
                    Intent textIntent = new Intent(Intent.ACTION_SEND);
                    textIntent.setType("text/plain");
                    textIntent.putExtra(Intent.EXTRA_TEXT, encrypted);
                    context.startActivity(Intent.createChooser(textIntent, context.getString(R.string.settings_backup_share_title)));
                })
                .show();
    }

    public static void showDebugDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setTitle("Debug info")
                .setMessage(Html.fromHtml(
                        "<b>workingMode</b> = " + GlobalValues.sWorkingMode + "<br>"
                                + "<b>backgroundUri</b> = " + GlobalValues.sBackgroundUri + "<br>"
                                + "<b>actionBarType</b> = " + GlobalValues.sActionBarType + "<br>"
                                + "<b>sortMode</b> = " + GlobalValues.sSortMode + "<br>"
                                + "<b>iconPack</b> = " + GlobalValues.sIconPack + "<br>"))
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton("LOGCAT", (dialogInterface, i) -> Settings.setLogger())
                .setCancelable(false)
                .show();
    }

    public static void showGrantPriviligedPermDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_ice_box_perm_not_support)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .setNeutralButton(R.string.dialog_go_to_perm_button, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$ManageApplicationsActivity"));
                    context.startActivity(intent);
                })
                .show();
    }

    public static void showDeleteAnywhereDialog(Context context, AnywhereEntity ae) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(context);
        builder.setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_delete_message), "<b>" + ae.getAppName() + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    AnywhereApplication.sRepository.delete(ae);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtils.removeShortcut(ae);
                        builder.setDismissParent(true);
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void showAddShortcutDialog(Context context, AnywhereDialogBuilder builder, AnywhereEntity ae, DialogInterface.OnClickListener listener) {
        builder.setTitle(R.string.dialog_add_shortcut_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_add_shortcut_message), "<b>" + ae.getAppName() + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showCannotAddShortcutDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_cant_add_shortcut_title)
                .setMessage(R.string.dialog_cant_add_shortcut_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static void showRemoveShortcutDialog(Context context, AnywhereEntity ae) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(context);
        builder.setTitle(R.string.dialog_remove_shortcut_title)
                .setMessage(Html.fromHtml(String.format(context.getString(R.string.dialog_remove_shortcut_message), "<b>" + ae.getAppName() + "</b>")))
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) -> {
                    ShortcutsUtils.removeShortcut(ae);
                    builder.setDismissParent(true);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null);
        builder.show();
    }

    public static void showDeleteSelectCardDialog(Context context, DialogInterface.OnClickListener listener) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(R.string.dialog_delete_selected_message)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showHasSameCardDialog(Context context, DialogInterface.OnClickListener listener) {
        new AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_same_app_name)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showHasNotGrantPermYetDialog(Context context, DialogInterface.OnClickListener listener) {
        new AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_perm_not_ever)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showCheckShizukuWorkingDialog(Context context) {
        new AnywhereDialogBuilder(context)
                .setMessage(R.string.dialog_message_shizuku_not_running)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                    if (intent != null) {
                        ((AppCompatActivity) context).startActivityForResult(intent, Const.REQUEST_CODE_SHIZUKU_PERMISSION);
                    } else {
                        ToastUtil.makeText(R.string.toast_not_install_shizuku);
                        URLSchemeHandler.parse(URLManager.SHIZUKU_COOLAPK_DOWNLOAD_PAGE, context);
                    }
                })
                .show();
    }

    public static void showGotoShizukuManagerDialog(Context context, DialogInterface.OnClickListener listener) {
        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showDeletePageDialog(Context context, String title, DialogInterface.OnClickListener listener, boolean isDeletePageAndItem) {
        Spanned message;
        if (isDeletePageAndItem) {
            message = Html.fromHtml(String.format(context.getString(R.string.dialog_delete_with_sub_item_message), "<b>" + title + "</b>"));
        } else {
            message = Html.fromHtml(String.format(context.getString(R.string.dialog_delete_message), "<b>" + title + "</b>"));
        }

        new AnywhereDialogBuilder(context)
                .setTitle(R.string.dialog_delete_selected_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, listener)
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .show();
    }

    public static void showPageListDialog(Context context, AnywhereEntity ae) {
        String[] items = new String[]{};
        List<PageEntity> list = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        if (list != null) {
            for (PageEntity pe : list) {
                items = TextUtils.insertStringArray(items, pe.getTitle());
            }
            AnywhereDialogBuilder builder = new AnywhereDialogBuilder(context);
            builder.setItems(items, (dialog, which) -> {
                ae.setCategory(list.get(which).getTitle());
                AnywhereApplication.sRepository.update(ae);
                builder.setDismissParent(true);
            });
            builder.show();
        }
    }

    public static void showColorPickerDialog(Context context, ColorPickerClickListener listener) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()
                .setPositiveButton(context.getString(R.string.dialog_delete_positive_button), listener)
                .setNegativeButton(context.getString(R.string.dialog_delete_negative_button), null)
                .build()
                .show();
    }

    public static void showIconPackChoosingDialog(AppCompatActivity activity) {
        IconPackDialogFragment fragment = new IconPackDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
    }

    public static void showDarkModeTimePickerDialog(AppCompatActivity activity) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
    }

    public static void showIntervalSetupDialog(AppCompatActivity activity) {
        IntervalDialogFragment fragment = new IntervalDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
    }

    public static void showRestoreApplyDialog(AppCompatActivity activity) {
        RestoreApplyFragmentDialog dialog = new RestoreApplyFragmentDialog();
        dialog.show(activity.getSupportFragmentManager(), dialog.getTag());
    }

    public static void showCreatePinnedShortcutDialog(AppCompatActivity activity, AnywhereEntity ae) {
        CreateShortcutDialogFragment fragment = new CreateShortcutDialogFragment(ae);
        fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
    }

    public static CardListDialogFragment showCardListDialog(AppCompatActivity activity) {
        CardListDialogFragment fragment = new CardListDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
        return fragment;
    }

    public static void showRenameDialog(AppCompatActivity activity, String title) {
        RenameFragmentDialog dialog = new RenameFragmentDialog(title);
        dialog.show(activity.getSupportFragmentManager(), dialog.getTag());
    }
}
