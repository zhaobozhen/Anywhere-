package com.absinthe.anywhere_.ui.backup

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.text.HtmlCompat
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
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
import rikka.recyclerview.fixEdgeEffect
import rikka.widget.borderview.BorderRecyclerView
import rikka.widget.borderview.BorderView

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
        findPreference<EditTextPreference>(Const.PREF_WEBDAV_PASSWORD)?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                GlobalValues.webdavPassword = newValue.toString()
                preference.summary = getPWString(newValue.toString())
                true
            }
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
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

    override fun onCreateRecyclerView(inflater: LayoutInflater, parent: ViewGroup, savedInstanceState: Bundle?): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState) as BorderRecyclerView
        recyclerView.fixEdgeEffect()
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        recyclerView.isVerticalScrollBarEnabled = false

        val lp = recyclerView.layoutParams
        if (lp is FrameLayout.LayoutParams) {
            lp.rightMargin = recyclerView.context.resources.getDimension(rikka.material.R.dimen.rd_activity_horizontal_margin).toInt()
            lp.leftMargin = lp.rightMargin
        }

        recyclerView.borderViewDelegate.borderVisibilityChangedListener =
            BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean -> (activity as BackupActivity?)?.appBar?.setRaised(!top) }

        return recyclerView
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