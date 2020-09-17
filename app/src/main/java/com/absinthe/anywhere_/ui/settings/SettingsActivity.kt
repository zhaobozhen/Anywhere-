package com.absinthe.anywhere_.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding
import com.absinthe.anywhere_.listener.OnDocumentResultListener
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.libraries.utils.extensions.paddingBottomCompat
import moe.shizuku.preference.ListPreference
import moe.shizuku.preference.Preference
import moe.shizuku.preference.PreferenceFragment
import moe.shizuku.preference.SwitchPreference

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

    class SettingsFragment : PreferenceFragment() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)

            //Normal
            (findPreference(Const.PREF_WORKING_MODE) as ListPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.workingMode = newValue as String
                    true
                }
            }

            //View
            (findPreference(Const.PREF_CHANGE_BACKGROUND) as Preference).apply {
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
            (findPreference(Const.PREF_RESET_BACKGROUND) as Preference).apply {
                setOnPreferenceClickListener {
                    DialogManager.showResetBackgroundDialog(requireActivity())
                    true
                }
            }
            (findPreference(Const.PREF_DARK_MODE) as ListPreference).apply {
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
            val streamCardModePreference = (findPreference(Const.PREF_STREAM_CARD_MODE) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isStreamCardMode = newValue as Boolean
                    GlobalValues.cardModeLiveData.value = newValue

                    (findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE) as SwitchPreference).apply {
                        isVisible = newValue
                    }
                    (findPreference(Const.PREF_CARD_BACKGROUND) as ListPreference).apply {
                        isVisible = newValue
                    }
                    true
                }
            }
            (findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE) as SwitchPreference).apply {
                isVisible = streamCardModePreference.isChecked
                isIconSpaceReserved = true
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isStreamCardModeSingleLine = newValue as Boolean
                    GlobalValues.cardModeLiveData.value = newValue
                    true
                }
            }
            (findPreference(Const.PREF_CARD_BACKGROUND) as ListPreference).apply {
                isVisible = streamCardModePreference.isChecked
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.sCardBackgroundMode = newValue.toString()
                    GlobalValues.cardModeLiveData.value = newValue
                    true
                }
            }
            (findPreference(Const.PREF_MD2_TOOLBAR) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isMd2Toolbar = newValue as Boolean
                    AppUtils.restart()
                    true
                }
            }
            (findPreference(Const.PREF_ICON_PACK) as Preference).apply {
                setOnPreferenceClickListener {
                    DialogManager.showIconPackChoosingDialog(requireActivity() as BaseActivity)
                    true
                }
            }

            //Advanced
            (findPreference(Const.PREF_PAGES) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isPages = newValue as Boolean
                    AppUtils.restart()
                    true
                }
            }
            (findPreference(Const.PREF_CLEAR_SHORTCUTS) as Preference).apply {
                if (!AppUtils.atLeastNMR1()) {
                    isVisible = false
                } else {
                    setOnPreferenceClickListener {
                        DialogManager.showClearShortcutsDialog(requireActivity())
                        true
                    }
                }
            }
            (findPreference(Const.PREF_TILES) as Preference).apply {
                if (!AppUtils.atLeastN()) {
                    isVisible = false
                }
            }
            (findPreference(Const.PREF_COLLECTOR_PLUS) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isCollectorPlus = newValue as Boolean
                    if (newValue) {
                        DialogManager.showIntervalSetupDialog(requireActivity() as BaseActivity)
                    }
                    true
                }
            }
            (findPreference(Const.PREF_EXCLUDE_FROM_RECENT) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isExcludeFromRecent = newValue as Boolean
                    true
                }
            }
            (findPreference(Const.PREF_SHOW_SHELL_RESULT) as SwitchPreference).apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.isShowShellResult = newValue as Boolean
                    true
                }
            }

            //Others
            (findPreference(Const.PREF_HELP) as Preference).apply {
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
            (findPreference(Const.PREF_BETA) as Preference).apply {
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

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.paddingBottomCompat = StatusBarUtil.getNavBarHeight()
        }

        override fun onCreateItemDecoration(): DividerDecoration? {
            return CategoryDivideDividerDecoration()
        }
    }

}