package com.absinthe.anywhere_.ui.backup;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BackupFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    static BackupFragment newInstance() {
        return new BackupFragment();
    }
    private Context mContext;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_backup, rootKey);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference backupPreference = findPreference(Const.PREF_BACKUP);
        Preference restorePreference = findPreference(Const.PREF_RESTORE);
        Preference sharePreference = findPreference(Const.PREF_BACKUP_SHARE);
        Preference applyPreference = findPreference(Const.PREF_RESTORE_APPLY);

        if (backupPreference != null) {
            backupPreference.setOnPreferenceClickListener(this);
        }
        if (restorePreference != null) {
            restorePreference.setOnPreferenceClickListener(this);
        }
        if (sharePreference != null) {
            sharePreference.setOnPreferenceClickListener(this);
        }
        if (applyPreference != null) {
            applyPreference.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case Const.PREF_BACKUP:
                if (StorageUtils.isExternalStorageWritable()) {
                    StorageUtils.createFile(BackupActivity.getInstance(), "*/*",
                            "Anywhere-Backups-" + TextUtils.getCurrFormatDate() + ".awbackups");
                } else {
                    ToastUtil.makeText(R.string.toast_check_device_storage_state);
                }
                return true;
            case Const.PREF_RESTORE:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                BackupActivity.getInstance().startActivityForResult(intent, Const.REQUEST_CODE_RESTORE_BACKUPS);
                return true;
            case Const.PREF_BACKUP_SHARE:
                String content = StorageUtils.ExportAnywhereEntityJsonString();
                String encrypted = CipherUtils.encrypt(content);

                if (encrypted != null) {
                    String dig = encrypted.length() > 50 ? encrypted.substring(0, 50) + "…" : encrypted;
                    new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                            .setTitle(R.string.settings_backup_share_title)
                            .setMessage(dig)
                            .setPositiveButton(R.string.btn_backup_copy, (dialog, which) -> {
                                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
                                startActivity(Intent.createChooser(textIntent, getString(R.string.settings_backup_share_title)));
                            })
                            .show();
                }
                return true;
            case Const.PREF_RESTORE_APPLY:
                RestoreApplyFragmentDialog dialog = new RestoreApplyFragmentDialog();
                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                        RestoreApplyFragmentDialog.class.getSimpleName());
                return true;
            default:
                break;
        }
        return false;
    }

}
