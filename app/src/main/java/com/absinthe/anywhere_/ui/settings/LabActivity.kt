package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivityLabBinding
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.utils.AppUtils

class LabActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLabBinding

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        mBinding = ActivityLabBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    class LabFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_lab, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            findPreference<Preference>(Const.PREF_PAGES)?.apply {
                onPreferenceChangeListener = this@LabFragment
            }
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            return false
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            when (preference.key) {
                Const.PREF_PAGES -> {
                    GlobalValues.setsIsPages(newValue as Boolean)
                    AppUtils.restart()
                    return true
                }
            }
            return false
        }
    }

}