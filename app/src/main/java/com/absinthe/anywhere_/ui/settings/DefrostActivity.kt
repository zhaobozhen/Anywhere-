package com.absinthe.anywhere_.ui.settings

import android.app.admin.DevicePolicyManager
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.defrost.DefrostAdapter
import com.absinthe.anywhere_.adapter.defrost.DefrostItem
import com.absinthe.anywhere_.constants.Const
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

        val adapter = DefrostAdapter().apply {
            setList(listOf(DefrostItem(Const.DEFROST_MODE_DSM, getString(R.string.defrost_mode_dsm), getString(R.string.defrost_mode_dsm_summary), getString(R.string.btn_acquire_permission)),
                    DefrostItem(Const.DEFROST_MODE_ICEBOX_SDK, getString(R.string.defrost_mode_icebox_sdk), getString(R.string.defrost_mode_icebox_sdk_summary), getString(R.string.btn_acquire_permission)),
                    DefrostItem(Const.DEFROST_MODE_DPM, getString(R.string.defrost_mode_dpm), getString(R.string.defrost_mode_dpm_summary), ""),
                    DefrostItem(Const.DEFROST_MODE_ROOT, getString(R.string.defrost_mode_root), getString(R.string.defrost_mode_root_summary), ""),
                    DefrostItem(Const.DEFROST_MODE_SHIZUKU, getString(R.string.defrost_mode_shizuku), getString(R.string.defrost_mode_shizuku_summary), "")))
            setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.button) {
                    if (position == 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            DSMClient.requestScopes(this@DefrostActivity, 1, DevicePolicyManager.DELEGATION_PACKAGE_ACCESS)
                        }
                    }
                }
            }
        }
        mBinding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(this@DefrostActivity)
        }
    }
}