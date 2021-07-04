package com.absinthe.anywhere_.ui.backup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.extension.addSystemBarPaddingAsync
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.utils.StorageUtils.createFile
import com.absinthe.anywhere_.utils.StorageUtils.exportAnywhereEntityJsonString
import com.absinthe.anywhere_.utils.StorageUtils.isExternalStorageWritable
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.DialogManager.showBackupShareDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showRestoreApplyDialog
import com.google.android.material.snackbar.Snackbar

const val BACKUP_TIP_VERSION = "2.0.0"

class BackupFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_backup, rootKey)

        findPreference<Preference>(Const.PREF_WEBDAV_HOST)?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                GlobalValues.webdavHost = newValue.toString()
                preference.summary = newValue.toString()
                true
            }
            summary = GlobalValues.webdavHost
        }
        findPreference<Preference>(Const.PREF_WEBDAV_USERNAME)?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                GlobalValues.webdavUsername = newValue.toString()
                preference.summary = newValue.toString()
                true
            }
            summary = GlobalValues.webdavUsername
            isIconSpaceReserved = true
        }
        findPreference<Preference>(Const.PREF_WEBDAV_PASSWORD)?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                GlobalValues.webdavPassword = newValue.toString()
                preference.summary = getPWString(newValue.toString())
                true
            }
            summary = getPWString(GlobalValues.webdavPassword)
            isIconSpaceReserved = true
        }
        findPreference<Preference>(Const.PREF_WEBDAV_BACKUP)?.apply {
            setOnPreferenceClickListener {
                if (GlobalValues.webdavHost.isEmpty() ||
                        GlobalValues.webdavUsername.isEmpty() ||
                        GlobalValues.webdavPassword.isEmpty()) {
                    Snackbar.make(listView, R.string.toast_check_webdav_configuration, Snackbar.LENGTH_LONG).show()
                } else {
                    StorageUtils.webdavBackup()
                }
                true
            }
            isIconSpaceReserved = true
        }
        findPreference<SwitchPreference>(Const.PREF_WEBDAV_AUTO_BACKUP)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.isAutoBackup = newValue as Boolean
                true
            }
            isIconSpaceReserved = true
        }
        findPreference<Preference>(Const.PREF_WEBDAV_RESTORE)?.apply {
            setOnPreferenceClickListener {
                if (GlobalValues.webdavHost.isEmpty() ||
                        GlobalValues.webdavUsername.isEmpty() ||
                        GlobalValues.webdavPassword.isEmpty()) {
                    Snackbar.make(listView, R.string.toast_check_webdav_configuration, Snackbar.LENGTH_LONG).show()
                } else {
                    DialogManager.showWebdavRestoreDialog(requireActivity() as BaseActivity<*>)
                }
                true
            }
            isIconSpaceReserved = true
        }
        findPreference<Preference>(Const.PREF_BACKUP)?.apply {
            setOnPreferenceClickListener {
                if (isExternalStorageWritable) {
                    createFile(requireActivity() as BaseActivity<*>, "*/*",
                            "Anywhere-Backups-" + AppTextUtils.currentFormatDate + ".awbackups")
                } else {
                    ToastUtil.makeText(R.string.toast_check_device_storage_state)
                }
                true
            }
        }
        findPreference<Preference>(Const.PREF_RESTORE)?.apply {
            setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                requireActivity().startActivityForResult(intent, Const.REQUEST_CODE_RESTORE_BACKUPS)
                true
            }
        }
        findPreference<Preference>(Const.PREF_BACKUP_SHARE)?.apply {
            setOnPreferenceClickListener {
                exportAnywhereEntityJsonString()?.let { content ->
                    CipherUtils.encrypt(content)?.let {
                        val dig = if (it.length > 50) it.substring(0, 50) + "…" else it
                        showBackupShareDialog(requireActivity(), dig, it)
                    }
                }
                true
            }
        }
        findPreference<Preference>(Const.PREF_RESTORE_APPLY)?.apply {
            setOnPreferenceClickListener {
                showRestoreApplyDialog(requireActivity() as BaseActivity<*>)
                true
            }
        }
        findPreference<Preference>("backupTip")?.apply {
            summary = getBackupTip()
        }
        findPreference<Preference>("backupTip2")?.apply {
            summary = HtmlCompat.fromHtml(getString(R.string.settings_backup_tip2), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.addSystemBarPaddingAsync(addStatusBarPadding = false)
    }

    private fun getBackupTip(): CharSequence {
        return HtmlCompat.fromHtml(String.format(getString(R.string.settings_backup_tip), BACKUP_TIP_VERSION, BACKUP_TIP_VERSION), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun getPWString(text: String): String {
        val sb = StringBuilder().apply {
            for (char in text) {
                append("●")
            }
        }

        return sb.toString()
    }
}