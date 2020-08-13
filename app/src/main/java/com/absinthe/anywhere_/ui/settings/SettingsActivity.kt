package com.absinthe.anywhere_.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.IzukoHelper
import com.absinthe.anywhere_.utils.manager.URLManager

class SettingsActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySettingsBinding

    override fun setViewBinding() {
        isPaddingToolbar = true
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            //Normal
            findPreference<DropDownPreference>(Const.PREF_WORKING_MODE)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }

            //View
            findPreference<Preference>(Const.PREF_CHANGE_BACKGROUND)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_RESET_BACKGROUND)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }
            findPreference<DropDownPreference>(Const.PREF_DARK_MODE)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            val streamCardModePreference = findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_MODE)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_SINGLE_LINE)?.apply {
                isEnabled = streamCardModePreference?.isChecked ?: false
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<DropDownPreference>(Const.PREF_CARD_BACKGROUND)?.apply {
                isEnabled = findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_MODE)?.isChecked
                        ?: false
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_MD2_TOOLBAR)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_ICON_PACK)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_CARD_LAYOUT)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }

            //Advanced
            findPreference<Preference>(Const.PREF_PAGES)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_CLEAR_SHORTCUTS)?.apply {
                onPreferenceClickListener = this@SettingsFragment

                if (!AppUtils.atLeastNMR1()) {
                    isVisible = false
                }
            }
            findPreference<Preference>(Const.PREF_TILES)?.apply {
                if (!AppUtils.atLeastN()) {
                    isVisible = false
                }
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_COLLECTOR_PLUS)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_EXCLUDE_FROM_RECENT)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_SHOW_SHELL_RESULT)?.apply {
                onPreferenceChangeListener = this@SettingsFragment
            }

            //Others
            findPreference<Preference>(Const.PREF_HELP)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_BETA)?.apply {
                onPreferenceClickListener = this@SettingsFragment
            }
            findPreference<Preference>(Const.PREF_GIFT)?.apply {
                if (!BuildConfig.DEBUG) {
                    isVisible = false
                }
                summary = if (IzukoHelper.isHitagi) {
                    getText(R.string.settings_gift_purchase_summary)
                } else {
                    getText(R.string.settings_gift_summary)
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.setPadding(0, 0, 0, StatusBarUtil.getNavBarHeight())
        }

        override fun onResume() {
            super.onResume()

            findPreference<Preference>(Const.PREF_GIFT)?.apply {
                summary = if (IzukoHelper.isHitagi) {
                    getText(R.string.settings_gift_purchase_summary)
                } else {
                    getText(R.string.settings_gift_summary)
                }
            }
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            when (preference.key) {
                Const.PREF_CHANGE_BACKGROUND -> {
                    try {
                        if (IzukoHelper.isHitagi) {
                            startActivity(Intent(requireActivity(), BackgroundActivity::class.java))
                        } else {
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
                        }
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.makeText(R.string.toast_no_document_app)
                    }
                    return true
                }
                Const.PREF_RESET_BACKGROUND -> {
                    DialogManager.showResetBackgroundDialog(requireActivity())
                    return true
                }
                Const.PREF_HELP -> {
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
                    return true
                }
                Const.PREF_BETA -> {
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
                    return true
                }
                Const.PREF_CLEAR_SHORTCUTS -> {
                    DialogManager.showClearShortcutsDialog(requireActivity())
                    return true
                }
                Const.PREF_ICON_PACK -> {
                    DialogManager.showIconPackChoosingDialog(requireActivity() as BaseActivity)
                    return true
                }
                Const.PREF_CARD_LAYOUT -> {
                    return true
                }
            }
            return false
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            when (preference.key) {
                Const.PREF_WORKING_MODE -> GlobalValues.workingMode = newValue as String
                Const.PREF_DARK_MODE -> if (newValue.toString() == Const.DARK_MODE_AUTO) {
                    DialogManager.showDarkModeTimePickerDialog(requireActivity() as BaseActivity)
                } else {
                    Settings.setTheme(newValue.toString())
                    GlobalValues.darkMode = newValue.toString()
                }
                Const.PREF_STREAM_CARD_MODE -> {
                    GlobalValues.isStreamCardMode = newValue as Boolean
                    GlobalValues.cardModeLiveData.value = newValue

                    findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_SINGLE_LINE)?.apply {
                        isEnabled = newValue
                    }
                    findPreference<DropDownPreference>(Const.PREF_CARD_BACKGROUND)?.apply {
                        isEnabled = newValue
                    }
                    return true
                }
                Const.PREF_STREAM_CARD_SINGLE_LINE -> {
                    GlobalValues.isStreamCardModeSingleLine = newValue as Boolean
                    GlobalValues.cardModeLiveData.value = newValue
                    return true
                }
                Const.PREF_CARD_BACKGROUND -> {
                    GlobalValues.sCardBackgroundMode = newValue.toString()
                    GlobalValues.cardModeLiveData.value = newValue
                    return true
                }
                Const.PREF_COLLECTOR_PLUS -> {
                    GlobalValues.isCollectorPlus = newValue as Boolean
                    if (newValue) {
                        DialogManager.showIntervalSetupDialog(requireActivity() as BaseActivity)
                    }
                    return true
                }
                Const.PREF_MD2_TOOLBAR -> {
                    GlobalValues.isMd2Toolbar = newValue as Boolean
                    AppUtils.restart()
                    return true
                }
                Const.PREF_EXCLUDE_FROM_RECENT -> {
                    GlobalValues.isExcludeFromRecent = newValue as Boolean
                    return true
                }
                Const.PREF_SHOW_SHELL_RESULT -> {
                    GlobalValues.isShowShellResult = newValue as Boolean
                    return true
                }
                Const.PREF_PAGES -> {
                    GlobalValues.isPages = newValue as Boolean
                    AppUtils.restart()
                    return true
                }
            }
            return true
        }
    }

}