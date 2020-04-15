package com.absinthe.anywhere_.ui.backup

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.StorageUtils.createFile
import com.absinthe.anywhere_.utils.StorageUtils.exportAnywhereEntityJsonString
import com.absinthe.anywhere_.utils.StorageUtils.isExternalStorageWritable
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager.showBackupShareDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showRestoreApplyDialog

class BackupFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_backup, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        findPreference<Preference>(Const.PREF_BACKUP)?.apply {
            onPreferenceClickListener = this@BackupFragment
        }
        findPreference<Preference>(Const.PREF_RESTORE)?.apply {
            onPreferenceClickListener = this@BackupFragment
        }
        findPreference<Preference>(Const.PREF_BACKUP_SHARE)?.apply {
            onPreferenceClickListener = this@BackupFragment
        }
        findPreference<Preference>(Const.PREF_RESTORE_APPLY)?.apply {
            onPreferenceClickListener = this@BackupFragment
        }
        findPreference<Preference>("backupTip")?.apply {
            summary = getBackupTip("1.7.0")
        }

    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            Const.PREF_BACKUP -> {
                if (isExternalStorageWritable) {
                    createFile(requireActivity() as BaseActivity, "*/*",
                            "Anywhere-Backups-" + TextUtils.getCurrFormatDate() + ".awbackups")
                } else {
                    ToastUtil.makeText(R.string.toast_check_device_storage_state)
                }
                return true
            }
            Const.PREF_RESTORE -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                requireActivity().startActivityForResult(intent, Const.REQUEST_CODE_RESTORE_BACKUPS)
                return true
            }
            Const.PREF_BACKUP_SHARE -> {
                exportAnywhereEntityJsonString()?.let { content ->
                    CipherUtils.encrypt(content)?.let {
                        val dig = if (it.length > 50) it.substring(0, 50) + "…" else it
                        showBackupShareDialog(requireActivity(), dig, it)
                    }
                }
                return true
            }
            Const.PREF_RESTORE_APPLY -> {
                showRestoreApplyDialog(requireActivity() as BaseActivity)
                return true
            }
        }
        return false
    }

    private fun getBackupTip(versionName: String): String {
        return String.format(getString(R.string.settings_backup_tip), versionName, versionName)
    }
}