package com.absinthe.anywhere_.ui.settings

import com.absinthe.anywhere_.BaseActivity
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
}