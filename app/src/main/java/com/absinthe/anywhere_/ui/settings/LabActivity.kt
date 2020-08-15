package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
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

class LabActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLabBinding

    override fun setViewBinding() {
        isPaddingToolbar = true
        mBinding = ActivityLabBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    class LabFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_lab, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            findPreference<SwitchPreferenceCompat>(Const.PREF_TRANS_ICON)?.apply {
                onPreferenceChangeListener = this@LabFragment
            }
            findPreference<SwitchPreferenceCompat>(Const.PREF_EDITOR_ENTRY_ANIM)?.apply {
                onPreferenceChangeListener = this@LabFragment
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            when(preference.key) {
                Const.PREF_TRANS_ICON -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(500)
                        AppUtils.setTransparentLauncherIcon(requireContext(), newValue as Boolean)
                    }
                    return true
                }
                Const.PREF_EDITOR_ENTRY_ANIM -> {
                    GlobalValues.editorEntryAnim = newValue as Boolean
                    return true
                }
            }
            return false
        }
    }

}