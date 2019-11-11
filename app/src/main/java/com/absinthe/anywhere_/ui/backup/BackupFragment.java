package com.absinthe.anywhere_.ui.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.ToastUtil;

public class BackupFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    static BackupFragment newInstance() {
        return new BackupFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_backup, rootKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference backupPreference = findPreference(Const.SP_KEY_BACKUP);
        Preference restorePreference = findPreference(Const.SP_KEY_RESTORE);

        if (backupPreference != null) {
            backupPreference.setOnPreferenceClickListener(this);
        }
        if (restorePreference != null) {
            restorePreference.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case Const.SP_KEY_BACKUP:
                if (StorageUtils.isExternalStorageWritable()) {
                    StorageUtils.createFile(BackupActivity.getInstance(), "*/*",
                            "Anywhere-Backups-" + TextUtils.getCurrFormatDate() + ".awbackups");
                } else {
                    ToastUtil.makeText("请检查设备存储状态");
                }
                return true;
            case Const.SP_KEY_RESTORE:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                BackupActivity.getInstance().startActivityForResult(intent, Const.REQUEST_CODE_RESTORE_BACKUPS);
                return true;
            default:
                break;
        }
        return false;
    }

}
