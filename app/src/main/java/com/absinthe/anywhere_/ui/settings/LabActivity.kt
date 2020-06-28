package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivityLabBinding

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
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            return false
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            return false
        }
    }

}