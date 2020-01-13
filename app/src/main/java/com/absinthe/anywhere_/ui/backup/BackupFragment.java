package com.absinthe.anywhere_.ui.backup;

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
import com.absinthe.anywhere_.utils.manager.DialogManager;

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
                    DialogManager.showBackupShareDialog(mContext, dig, encrypted);
                }
                return true;
            case Const.PREF_RESTORE_APPLY:
                DialogManager.showRestoreApplyDialog((AppCompatActivity) mContext);
                return true;
            default:
                break;
        }
        return false;
    }

}
