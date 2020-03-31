package com.absinthe.anywhere_.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.ui.main.MainFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager.showClearShortcutsDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showDarkModeTimePickerDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showIconPackChoosingDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showIntervalSetupDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showResetBackgroundDialog
import com.absinthe.anywhere_.utils.manager.IzukoHelper.isHitagi
import com.absinthe.anywhere_.utils.manager.URLManager

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val workingModePreference = findPreference<DropDownPreference>(Const.PREF_WORKING_MODE)
        val darkModePreference = findPreference<DropDownPreference>(Const.PREF_DARK_MODE)
        val cardBackgroundPreference = findPreference<DropDownPreference>(Const.PREF_CARD_BACKGROUND)
        val changeBgPreference = findPreference<Preference>(Const.PREF_CHANGE_BACKGROUND)
        val resetBgPreference = findPreference<Preference>(Const.PREF_RESET_BACKGROUND)
        val helpPreference = findPreference<Preference>(Const.PREF_HELP)
        val clearShortcutsPreference = findPreference<Preference>(Const.PREF_CLEAR_SHORTCUTS)
        val iconPackPreference = findPreference<Preference>(Const.PREF_ICON_PACK)
        val tilesPreference = findPreference<Preference>(Const.PREF_TILES)
        val giftPreference = findPreference<Preference>(Const.PREF_GIFT)
        val streamCardModePreference = findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_MODE)
        val streamCardSingleLinePreference = findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_SINGLE_LINE)
        val collectorPlusPreference = findPreference<SwitchPreferenceCompat>(Const.PREF_COLLECTOR_PLUS)
        val md2Preference = findPreference<SwitchPreferenceCompat>(Const.PREF_MD2_TOOLBAR)
        val excludePreference = findPreference<SwitchPreferenceCompat>(Const.PREF_EXCLUDE_FROM_RECENT)
        val showShellResultPreference = findPreference<SwitchPreferenceCompat>(Const.PREF_SHOW_SHELL_RESULT)

        workingModePreference?.onPreferenceChangeListener = this
        changeBgPreference?.onPreferenceClickListener = this
        resetBgPreference?.onPreferenceClickListener = this
        darkModePreference?.onPreferenceChangeListener = this
        helpPreference?.onPreferenceClickListener = this
        clearShortcutsPreference?.onPreferenceClickListener = this

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            clearShortcutsPreference?.isVisible = false
        }

        streamCardModePreference?.onPreferenceChangeListener = this

        streamCardSingleLinePreference?.isEnabled = streamCardModePreference?.isChecked ?: false
        streamCardSingleLinePreference?.onPreferenceChangeListener = this
        cardBackgroundPreference?.isEnabled = streamCardModePreference?.isChecked ?: false
        cardBackgroundPreference?.onPreferenceChangeListener = this

        iconPackPreference?.onPreferenceClickListener = this

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            tilesPreference?.isVisible = false
        }

        collectorPlusPreference?.onPreferenceChangeListener = this

        giftPreference?.summary = if (isHitagi) {
            getText(R.string.settings_gift_purchase_summary)
        } else {
            getText(R.string.settings_gift_summary)
        }

        md2Preference?.onPreferenceChangeListener = this
        excludePreference?.onPreferenceChangeListener = this
        showShellResultPreference?.onPreferenceChangeListener = this

    }

    override fun onResume() {
        super.onResume()
        val giftPreference = findPreference<Preference>(Const.PREF_GIFT)
        giftPreference?.summary = if (isHitagi) {
            getText(R.string.settings_gift_purchase_summary)
        } else {
            getText(R.string.settings_gift_summary)
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            Const.PREF_CHANGE_BACKGROUND -> {
                try {
                    if (isHitagi) {
                        requireActivity().startActivity(Intent(requireActivity(), BackgroundActivity::class.java))
                    } else {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        requireActivity().startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
                        (requireActivity() as BaseActivity).setDocumentResultListener(object : OnDocumentResultListener {
                            override fun onResult(uri: Uri) {
                                GlobalValues.setsBackgroundUri(uri.toString())
                                GlobalValues.setsActionBarType("")
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
                showResetBackgroundDialog(requireActivity())
                return true
            }
            Const.PREF_HELP -> {
                try {
                    CustomTabsIntent.Builder().build().apply {
                        launchUrl(requireActivity(), Uri.parse(URLManager.OLD_DOCUMENT_PAGE))
                    }
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(URLManager.OLD_DOCUMENT_PAGE)
                    }
                    requireActivity().startActivity(intent)
                }
                return true
            }
            Const.PREF_CLEAR_SHORTCUTS -> {
                showClearShortcutsDialog(requireActivity())
                return true
            }
            Const.PREF_ICON_PACK -> {
                showIconPackChoosingDialog(requireActivity() as BaseActivity)
                return true
            }
        }
        return false
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            Const.PREF_WORKING_MODE -> GlobalValues.sWorkingMode?.setValue(newValue as String)
            Const.PREF_DARK_MODE -> if (newValue.toString() == Const.DARK_MODE_AUTO) {
                showDarkModeTimePickerDialog(requireActivity() as BaseActivity)
            } else {
                Settings.setTheme(newValue.toString())
                GlobalValues.setsDarkMode(newValue.toString())
            }
            Const.PREF_STREAM_CARD_MODE -> {
                GlobalValues.setsIsStreamCardMode(newValue as Boolean)
                MainFragment.getCardMode().value = newValue.toString()

                findPreference<SwitchPreferenceCompat>(Const.PREF_STREAM_CARD_SINGLE_LINE)?.apply {
                    isEnabled = newValue
                }
                findPreference<DropDownPreference>(Const.PREF_CARD_BACKGROUND)?.apply {
                    isEnabled = newValue
                }
                return true
            }
            Const.PREF_STREAM_CARD_SINGLE_LINE -> {
                GlobalValues.setsIsStreamCardModeSingleLine(newValue as Boolean)
                MainFragment.getCardMode().value = newValue.toString()
                return true
            }
            Const.PREF_CARD_BACKGROUND -> {
                GlobalValues.setsCardBackgroundMode(newValue.toString())
                MainFragment.getCardMode().value = newValue.toString()
                return true
            }
            Const.PREF_COLLECTOR_PLUS -> {
                GlobalValues.setsIsCollectorPlus(newValue as Boolean)
                if (newValue) {
                    showIntervalSetupDialog(requireActivity() as BaseActivity)
                }
                return true
            }
            Const.PREF_MD2_TOOLBAR -> {
                GlobalValues.setsIsMd2Toolbar(newValue as Boolean)
                AppUtils.restart()
                return true
            }
            Const.PREF_EXCLUDE_FROM_RECENT -> {
                GlobalValues.setsIsExcludeFromRecent(newValue as Boolean)
                return true
            }
            Const.PREF_SHOW_SHELL_RESULT -> {
                GlobalValues.setsIsShowShellResult(newValue as Boolean)
                return true
            }
        }
        return true
    }
}