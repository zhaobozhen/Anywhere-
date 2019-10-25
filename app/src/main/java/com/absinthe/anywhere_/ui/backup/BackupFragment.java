package com.absinthe.anywhere_.ui.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.settings.SettingsActivity;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.ToastUtil;

public class BackupFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{

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

        Preference backupPreference = findPreference("backup");
        Preference restorePreference = findPreference("restore");

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
            case "backup":
                if (StorageUtils.isExternalStorageWritable()) {
                    StorageUtils.createFile(BackupActivity.getInstance(), "*/*", System.currentTimeMillis() + ".awbackups");
                }
                break;
            case "restore":
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                BackupActivity.getInstance().startActivityForResult(intent, Const.REQUEST_CODE_RESTORE_BACKUPS);
                break;
            default:
                break;
        }
        return true;
    }

}
