package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivityDefrostBinding

class DefrostActivity : BaseActivity() {

    private lateinit var mBinding: ActivityDefrostBinding

    override fun setViewBinding() {
        mBinding = ActivityDefrostBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_defrost, rootKey)
        }
    }
}