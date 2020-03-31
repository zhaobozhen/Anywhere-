package com.absinthe.anywhere_.ui.settings

import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySettingsBinding

    init {
        isPaddingToolbar = false
    }

    override fun setViewBinding() {
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }
}