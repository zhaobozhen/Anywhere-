package com.absinthe.anywhere_.ui.backup

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityBackupBinding
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.blankj.utilcode.util.Utils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rikka.recyclerview.fixEdgeEffect
import rikka.widget.borderview.BorderRecyclerView
import rikka.widget.borderview.BorderView
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

const val BACKUP_TIP_VERSION = "2.0.0"

class BackupActivity : AppBarActivity<ActivityBackupBinding>() {

  override fun setViewBinding() = ActivityBackupBinding.inflate(layoutInflater)

  override fun getToolBar() = binding.toolbar.toolBar

  override fun getAppBarLayout() = binding.toolbar.appBar

  class BackupFragment : PreferenceFragmentCompat() {

    private lateinit var backupResultLauncher: ActivityResultLauncher<String>
    private lateinit var restoreResultLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
      super.onAttach(context)
      backupResultLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) {
          it?.let {
            try {
              Utils.getApp().contentResolver.openOutputStream(it)?.let { os ->
                StorageUtils.exportAnywhereEntityJsonString()?.let { content ->
                  CipherUtils.encrypt(content)?.let { encrypted ->
                    os.write(encrypted.toByteArray())
                    os.close()
                    ToastUtil.makeText(requireContext(), getString(R.string.toast_backup_success))
                  }
                }
              }
            } catch (e: IOException) {
              e.printStackTrace()
              ToastUtil.makeText(requireContext(), getString(R.string.toast_runtime_error))
            }
          }
        }
      restoreResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { data ->
          try {
            Utils.getApp().contentResolver.openInputStream(data)?.let { inputStream ->
              val reader = BufferedReader(InputStreamReader(inputStream))
              val stringBuilder = StringBuilder()
              var line: String?

              while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
              }

              lifecycleScope.launch(Dispatchers.IO) {
                StorageUtils.restoreFromJson(requireContext(), stringBuilder.toString())
              }

              inputStream.close()
              reader.close()
            }
          } catch (e: IOException) {
            e.printStackTrace()
          }
        }
      }
    }

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
            GlobalValues.webdavPassword.isEmpty()
          ) {
            Snackbar.make(listView, R.string.toast_check_webdav_configuration, Snackbar.LENGTH_LONG)
              .show()
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
            GlobalValues.webdavPassword.isEmpty()
          ) {
            Snackbar.make(listView, R.string.toast_check_webdav_configuration, Snackbar.LENGTH_LONG)
              .show()
          } else {
            DialogManager.showWebdavRestoreDialog(requireActivity() as BaseActivity<*>)
          }
          true
        }
        isIconSpaceReserved = true
      }
      findPreference<Preference>(Const.PREF_BACKUP)?.apply {
        setOnPreferenceClickListener {
          if (StorageUtils.isExternalStorageWritable) {
            runCatching {
              backupResultLauncher.launch("Anywhere-Backups-" + AppTextUtils.currentFormatDate + ".awbackups")
            }.onFailure {
              Timber.e(it)
              ToastUtil.makeText(context, "Document API not working")
            }
          } else {
            ToastUtil.makeText(R.string.toast_check_device_storage_state)
          }
          true
        }
      }
      findPreference<Preference>(Const.PREF_RESTORE)?.apply {
        setOnPreferenceClickListener {
          runCatching {
            restoreResultLauncher.launch("*/*")
          }.onFailure {
            Timber.e(it)
            ToastUtil.makeText(context, "Document API not working")
          }
          true
        }
      }
      findPreference<Preference>(Const.PREF_BACKUP_SHARE)?.apply {
        setOnPreferenceClickListener {
          StorageUtils.exportAnywhereEntityJsonString()?.let { content ->
            CipherUtils.encrypt(content)?.let {
              val dig = if (it.length > 50) it.substring(0, 50) + "…" else it
              DialogManager.showBackupShareDialog(requireActivity(), dig, it)
            }
          }
          true
        }
      }
      findPreference<Preference>(Const.PREF_RESTORE_APPLY)?.apply {
        setOnPreferenceClickListener {
          DialogManager.showRestoreApplyDialog(requireActivity() as BaseActivity<*>)
          true
        }
      }
      findPreference<Preference>("backupTip")?.apply {
        summary = getBackupTip()
      }
      findPreference<Preference>("backupTip2")?.apply {
        summary = HtmlCompat.fromHtml(
          getString(R.string.settings_backup_tip2),
          HtmlCompat.FROM_HTML_MODE_LEGACY
        )
      }
    }

    override fun onCreateRecyclerView(
      inflater: LayoutInflater,
      parent: ViewGroup,
      savedInstanceState: Bundle?
    ): RecyclerView {
      val recyclerView =
        super.onCreateRecyclerView(inflater, parent, savedInstanceState) as BorderRecyclerView
      recyclerView.fixEdgeEffect()
      recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
      recyclerView.isVerticalScrollBarEnabled = false

      val lp = recyclerView.layoutParams
      if (lp is FrameLayout.LayoutParams) {
        lp.rightMargin =
          recyclerView.context.resources.getDimension(rikka.material.R.dimen.rd_activity_horizontal_margin)
            .toInt()
        lp.leftMargin = lp.rightMargin
      }

      recyclerView.borderViewDelegate.borderVisibilityChangedListener =
        BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
          (activity as BackupActivity?)?.appBar?.setRaised(
            !top
          )
        }

      return recyclerView
    }

    private fun getBackupTip(): CharSequence {
      return HtmlCompat.fromHtml(
        String.format(
          getString(R.string.settings_backup_tip),
          BACKUP_TIP_VERSION,
          BACKUP_TIP_VERSION
        ), HtmlCompat.FROM_HTML_MODE_LEGACY
      )
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
}
