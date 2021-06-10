package com.absinthe.anywhere_.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding
import com.absinthe.anywhere_.extension.addSystemBarPaddingAsync
import com.absinthe.anywhere_.listener.OnDocumentResultListener
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.URLManager
import rikka.preference.SimpleMenuPreference

class SettingsActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySettingsBinding

    companion object {
        init {
            SimpleMenuPreference.setLightFixEnabled(true)
        }
    }

    override fun setViewBinding() {
        isPaddingToolbar = true
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.addSystemBarPaddingAsync(addStatusBarPadding = false)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)

            //Normal
            findPreference<ListPreference>(Const.PREF_WORKING_MODE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.workingMode = newValue as String
                    true
                }
            }
            findPreference<SwitchPreference>(Const.PREF_CLOSE_AFTER_LAUNCH)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.closeAfterLaunch = newValue as Boolean
                    true
                }
            }

            //View
            findPreference<Preference>(Const.PREF_CHANGE_BACKGROUND)?.apply {
                setOnPreferenceClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        requireActivity().startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
                        (requireActivity() as BaseActivity).setDocumentResultListener(object : OnDocumentResultListener {
                            override fun onResult(uri: Uri) {
                                GlobalValues.backgroundUri = uri.toString()
                                GlobalValues.clearActionBarType()
                                AppUtils.restart()
                            }
                        })
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.makeText(R.string.toast_no_document_app)
                    }
                    true
                }
            }
            findPreference<Preference>(Const.PREF_RESET_BACKGROUND)?.apply {
                setOnPreferenceClickListener {
                    DialogManager.showResetBackgroundDialog(requireActivity())
                    true
                }
            }
            findPreference<ListPreference>(Const.PREF_DARK_MODE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    if (newValue.toString() == Const.DARK_MODE_AUTO) {
                        DialogManager.showDarkModeTimePickerDialog(requireActivity() as BaseActivity)
                    } else {
                        Settings.setTheme(newValue.toString())
                        GlobalValues.darkMode = newValue.toString()
                    }
                    true
                }
            }
            findPreference<ListPreference>(Const.PREF_CARD_MODE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.cardMode = newValue.toString()
                    GlobalValues.cardModeLiveData.value = newValue
                    true
                }
            }
            findPreference<ListPreference>(Const.PREF_CARD_BACKGROUND)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.sCardBackgroundMode = newValue.toString()
                    GlobalValues.cardModeLiveData.value = newValue
                    true
                }
            }
            findPreference<SwitchPreference>(Const.PREF_MD2_TOOLBAR)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isMd2Toolbar = newValue as Boolean
                    AppUtils.restart()
                    true
                }
            }
            findPreference<Preference>(Const.PREF_ICON_PACK)?.apply {
                setOnPreferenceClickListener {
                    DialogManager.showIconPackChoosingDialog(requireActivity() as BaseActivity)
                    true
                }
            }

            //Advanced
            findPreference<SwitchPreference>(Const.PREF_PAGES)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isPages = newValue as Boolean
                    AppUtils.restart()
                    true
                }
            }
            findPreference<Preference>(Const.PREF_CLEAR_SHORTCUTS)?.apply {
                if (!AppUtils.atLeastNMR1()) {
                    isVisible = false
                } else {
                    setOnPreferenceClickListener {
                        DialogManager.showClearShortcutsDialog(requireActivity())
                        true
                    }
                }
            }
            findPreference<Preference>(Const.PREF_TILES)?.apply {
                if (!AppUtils.atLeastN()) {
                    isVisible = false
                }
            }
            findPreference<SwitchPreference>(Const.PREF_COLLECTOR_PLUS)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isCollectorPlus = newValue as Boolean
                    if (newValue) {
                        DialogManager.showIntervalSetupDialog(requireActivity() as BaseActivity)
                    }
                    true
                }
            }
            findPreference<SwitchPreference>(Const.PREF_EXCLUDE_FROM_RECENT)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isExcludeFromRecent = newValue as Boolean
                    true
                }
            }
            findPreference<ListPreference>(Const.PREF_SHOW_SHELL_RESULT_MODE)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.showShellResultMode = newValue as String
                    true
                }
            }

            //Others
            findPreference<Preference>(Const.PREF_HELP)?.apply {
                setOnPreferenceClickListener {
                    try {
                        CustomTabsIntent.Builder().build().apply {
                            launchUrl(requireActivity(), URLManager.DOCUMENT_PAGE.toUri())
                        }
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = URLManager.DOCUMENT_PAGE.toUri()
                        }
                        requireActivity().startActivity(intent)
                    }
                    true
                }
            }
            findPreference<Preference>(Const.PREF_BETA)?.apply {
                setOnPreferenceClickListener {
                    try {
                        CustomTabsIntent.Builder().build().apply {
                            launchUrl(requireActivity(), URLManager.BETA_DISTRIBUTE_URL.toUri())
                        }
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        try {
                            URLSchemeHandler.parse(requireActivity(), URLManager.BETA_DISTRIBUTE_URL)
                        } catch (e: ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        }
                    }
                    true
                }
            }
        }
    }

}