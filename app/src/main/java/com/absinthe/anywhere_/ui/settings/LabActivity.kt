package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityLabBinding
import com.absinthe.anywhere_.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rikka.preference.SimpleMenuPreference

class LabActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLabBinding

    companion object {
        init {
            SimpleMenuPreference.setLightFixEnabled(true)
        }
    }

    override fun setViewBinding() {
        isPaddingToolbar = true
        mBinding = ActivityLabBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    class LabFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_lab, rootKey)

            findPreference<SwitchPreference>(Const.PREF_TRANS_ICON)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(500)
                        AppUtils.setTransparentLauncherIcon(requireContext(), newValue as Boolean)
                    }
                    true
                }
            }
            findPreference<SwitchPreference>(Const.PREF_EDITOR_ENTRY_ANIM)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.editorEntryAnim = newValue as Boolean
                    true
                }
            }
            findPreference<SwitchPreference>(Const.PREF_DEPRECATED_SC_CREATING_METHOD)?.apply {
                setOnPreferenceChangeListener { _, newValue ->
                    GlobalValues.deprecatedScCreatingMethod = newValue as Boolean
                    true
                }
            }
        }
    }

}