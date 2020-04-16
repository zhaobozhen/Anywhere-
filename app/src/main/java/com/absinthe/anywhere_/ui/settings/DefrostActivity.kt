package com.absinthe.anywhere_.ui.settings

import android.app.admin.DevicePolicyManager
import android.os.Build
import android.os.Bundle
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.databinding.ActivityDefrostBinding
import com.catchingnow.delegatedscopeclient.DSMClient

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

        mBinding.tvSummary.text = DSMClient.getOwnerPackageName(this)

        mBinding.btnGrantDsm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DSMClient.requestScopes(this, 1, DevicePolicyManager.DELEGATION_PACKAGE_ACCESS)
            }
        }
    }
}