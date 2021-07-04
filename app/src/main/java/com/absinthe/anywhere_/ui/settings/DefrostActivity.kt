package com.absinthe.anywhere_.ui.settings

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.defrost.DefrostAdapter
import com.absinthe.anywhere_.adapter.defrost.DefrostItem
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.databinding.ActivityDefrostBinding
import com.absinthe.anywhere_.receiver.AdminReceiver
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager.showGrantPrivilegedPermDialog
import com.absinthe.libraries.utils.utils.XiaomiUtilities
import com.blankj.utilcode.util.AppUtils
import com.catchingnow.delegatedscopeclient.DSMClient
import com.catchingnow.icebox.sdk_client.IceBox
import rikka.widget.borderview.BorderView

class DefrostActivity : AppBarActivity<ActivityDefrostBinding>() {

    private var mAdapter = DefrostAdapter()
    private lateinit var mList: List<DefrostItem>

    override fun setViewBinding() = ActivityDefrostBinding.inflate(layoutInflater)

    override fun getToolBar() = binding.toolbar.toolbar

    override fun getAppBarLayout() = binding.toolbar.appbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        mAdapter.apply {
            setList(mList)
            setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.button) {
                    if (mList[position].mode == Const.DEFROST_MODE_DSM) {
                        if (com.absinthe.anywhere_.utils.AppUtils.atLeastO()) {
                            try {
                                DSMClient.requestScopes(this@DefrostActivity, Const.REQUEST_CODE_DSM, DevicePolicyManager.DELEGATION_PACKAGE_ACCESS)
                            } catch (e: ActivityNotFoundException) {
                                ToastUtil.makeText(R.string.toast_dsm_not_support)
                            }
                        }
                    } else if (mList[position].mode == Const.DEFROST_MODE_ICEBOX_SDK) {
                        if (ContextCompat.checkSelfPermission(this@DefrostActivity, IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                            if (XiaomiUtilities.isMIUI()) {
                                showGrantPrivilegedPermDialog(this@DefrostActivity)
                            } else {
                                ActivityCompat.requestPermissions(this@DefrostActivity, arrayOf(IceBox.SDK_PERMISSION), Const.REQUEST_CODE_ICEBOX)
                            }
                        }
                    } else if (mList[position].mode == Const.DEFROST_MODE_DPM) {
                        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

                        if (devicePolicyManager.isAdminActive(AdminReceiver.componentName)) {
                            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, AdminReceiver.componentName)
                                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.defrost_mode_dpm_desc))
                            }
                            startActivityForResult(intent, Const.REQUEST_CODE_DPM)
                        } else {
                            ToastUtil.makeText(R.string.toast_this_app_not_dpm)
                        }
                    }
                }
            }
        }
        binding.list.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@DefrostActivity)
            borderVisibilityChangedListener =
                BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                    appBar?.setRaised(!top)
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isEmpty()) return

        if (grantResults[0] == Activity.RESULT_OK) {
            if (requestCode == Const.REQUEST_CODE_ICEBOX ||
                requestCode == Const.REQUEST_CODE_DSM ||
                requestCode == Const.REQUEST_CODE_DPM) {
                mAdapter.notifyDataSetChanged()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initData() {
        mList = if (com.absinthe.anywhere_.utils.AppUtils.atLeastO()) {
            listOf(
                    DefrostItem(Const.DEFROST_MODE_DSM, getString(R.string.defrost_mode_dsm), getString(R.string.defrost_mode_dsm_summary), getString(R.string.btn_acquire_permission), AppUtils.getAppName(DSMClient.getOwnerPackageName(this))),
                    DefrostItem(Const.DEFROST_MODE_ICEBOX_SDK, getString(R.string.defrost_mode_icebox_sdk), getString(R.string.defrost_mode_icebox_sdk_summary), getString(R.string.btn_acquire_permission), ""),
                    DefrostItem(Const.DEFROST_MODE_DPM, getString(R.string.defrost_mode_dpm), getString(R.string.defrost_mode_dpm_summary), getString(R.string.btn_acquire_permission), HtmlCompat.fromHtml(getString(R.string.defrost_mode_dpm_addition), HtmlCompat.FROM_HTML_MODE_LEGACY)),
                    DefrostItem(Const.DEFROST_MODE_ROOT, getString(R.string.defrost_mode_root), getString(R.string.defrost_mode_root_summary), "", ""),
                    DefrostItem(Const.DEFROST_MODE_SHIZUKU, getString(R.string.defrost_mode_shizuku), getString(R.string.defrost_mode_shizuku_summary), "", "")
            )
        } else {
            listOf(
                    DefrostItem(Const.DEFROST_MODE_ICEBOX_SDK, getString(R.string.defrost_mode_icebox_sdk), getString(R.string.defrost_mode_icebox_sdk_summary), getString(R.string.btn_acquire_permission), ""),
                    DefrostItem(Const.DEFROST_MODE_DPM, getString(R.string.defrost_mode_dpm), getString(R.string.defrost_mode_dpm_summary), getString(R.string.btn_acquire_permission), HtmlCompat.fromHtml(getString(R.string.defrost_mode_dpm_addition), HtmlCompat.FROM_HTML_MODE_LEGACY)),
                    DefrostItem(Const.DEFROST_MODE_ROOT, getString(R.string.defrost_mode_root), getString(R.string.defrost_mode_root_summary), "", ""),
                    DefrostItem(Const.DEFROST_MODE_SHIZUKU, getString(R.string.defrost_mode_shizuku), getString(R.string.defrost_mode_shizuku_summary), "", "")
            )
        }
    }
}