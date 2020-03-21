package com.absinthe.anywhere_.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.*
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.ShizukuHelper
import com.absinthe.anywhere_.viewmodel.InitializeViewModel
import com.blankj.utilcode.util.PermissionUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import moe.shizuku.api.ShizukuApiConstants
import timber.log.Timber
import java.util.*

class InitializeFragment : Fragment(), OnButtonCheckedListener {

    private lateinit var mBinding: FragmentInitializeBinding
    private lateinit var rootBinding: CardAcquireRootPermissionBinding
    private lateinit var shizukuBinding: CardAcquireShizukuPermissionBinding
    private lateinit var overlayBinding: CardAcquireOverlayPermissionBinding
    private lateinit var popupBinding: CardAcquirePopupPermissionBinding

    private var bRoot = false
    private var bShizuku = false
    private var bOverlay = false
    private var bPopup = false
    private var mWorkingMode: String = Const.WORKING_MODE_URL_SCHEME

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentInitializeBinding.inflate(inflater, container, false)
        rootBinding = CardAcquireRootPermissionBinding.inflate(inflater, container, false)
        shizukuBinding = CardAcquireShizukuPermissionBinding.inflate(inflater, container, false)
        overlayBinding = CardAcquireOverlayPermissionBinding.inflate(inflater, container, false)
        popupBinding = CardAcquirePopupPermissionBinding.inflate(inflater, container, false)
        initView()

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(InitializeViewModel::class.java)
        mWorkingMode = Const.WORKING_MODE_URL_SCHEME
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel!!.allPerm.value = Objects.requireNonNull(mViewModel!!.allPerm.value) or InitializeViewModel.OVERLAY_PERM
        }
        initObserver()
    }

    private fun initView() {
        bPopup = false
        bOverlay = bPopup
        bShizuku = bOverlay
        bRoot = bShizuku
        setHasOptionsMenu(true)
        mBinding.selectWorkingMode.toggleGroup.addOnButtonCheckedListener(this)
    }

    override fun onButtonChecked(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        Timber.d("onButtonChecked")
        when (checkedId) {
            R.id.btn_url_scheme -> if (isChecked) {
                actCards(CARD_ROOT, false)
                actCards(CARD_SHIZUKU, false)
                actCards(CARD_OVERLAY, false)
                actCards(CARD_POPUP, false)
                mWorkingMode = Const.WORKING_MODE_URL_SCHEME
            }
            R.id.btn_root -> if (isChecked) {
                actCards(CARD_SHIZUKU, false)
                actCards(CARD_ROOT, true)
                actCards(CARD_OVERLAY, true)
                actCards(CARD_POPUP, true)
                mWorkingMode = Const.WORKING_MODE_ROOT
            }
            R.id.btn_shizuku -> if (isChecked) {
                actCards(CARD_ROOT, false)
                actCards(CARD_SHIZUKU, true)
                actCards(CARD_OVERLAY, true)
                actCards(CARD_POPUP, true)
                mWorkingMode = Const.WORKING_MODE_SHIZUKU
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.initialize_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_initialize_done) {
            GlobalValues.setsWorkingMode(mWorkingMode)
            var flag = false
            val allPerm = Objects.requireNonNull(mViewModel!!.allPerm.value)
            when (mWorkingMode) {
                Const.WORKING_MODE_URL_SCHEME -> flag = true
                Const.WORKING_MODE_ROOT -> if (allPerm == InitializeViewModel.ROOT_PERM or InitializeViewModel.OVERLAY_PERM) {
                    flag = true
                }
                Const.WORKING_MODE_SHIZUKU -> if (allPerm == InitializeViewModel.SHIZUKU_GROUP_PERM or InitializeViewModel.OVERLAY_PERM) {
                    flag = true
                }
                else -> {
                }
            }
            if (flag) {
                enterMainFragment()
            } else {
                DialogManager.showHasNotGrantPermYetDialog(context) { _: DialogInterface?, _: Int -> enterMainFragment() }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enterMainFragment() {
        val fragment = MainFragment.newInstance(GlobalValues.sCategory)
        MainActivity.getInstance().viewModel.fragment.value = fragment
        MainActivity.getInstance().initFab()
        MainActivity.getInstance().initObserver()
    }

    private fun initObserver() {
        mViewModel!!.isRoot.observe(viewLifecycleOwner, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                rootBinding.btnAcquireRootPermission.setText(R.string.btn_acquired)
                rootBinding.btnAcquireRootPermission.isEnabled = false
                rootBinding.done.visibility = View.VISIBLE
                mViewModel!!.allPerm.value = Objects.requireNonNull(mViewModel!!.allPerm.value) or InitializeViewModel.ROOT_PERM
                Timber.d("allPerm = %s", mViewModel!!.allPerm.value)
            } else {
                Timber.d("ROOT permission denied.")
                ToastUtil.makeText(R.string.toast_root_permission_denied)
            }
        })
        mViewModel!!.isShizukuCheck.observe(viewLifecycleOwner, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                shizukuBinding.btnCheckShizukuState.setText(R.string.btn_checked)
                shizukuBinding.btnCheckShizukuState.isEnabled = false
                mViewModel!!.allPerm.value = Objects.requireNonNull(mViewModel!!.allPerm.value) or InitializeViewModel.SHIZUKU_CHECK_PERM
                shizukuBinding.btnAcquirePermission.isEnabled = true
            }
            if (Objects.requireNonNull(mViewModel!!.allPerm.value) and InitializeViewModel.SHIZUKU_GROUP_PERM == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                shizukuBinding.done.visibility = View.VISIBLE
            }
        })
        mViewModel!!.isShizuku.observe(viewLifecycleOwner, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                shizukuBinding.btnAcquirePermission.setText(R.string.btn_acquired)
                shizukuBinding.btnAcquirePermission.isEnabled = false
                mViewModel!!.allPerm.value = Objects.requireNonNull(mViewModel!!.allPerm.value) or InitializeViewModel.SHIZUKU_PERM
            }
            if (mViewModel!!.allPerm.value and InitializeViewModel.SHIZUKU_GROUP_PERM == InitializeViewModel.SHIZUKU_GROUP_PERM) {
                shizukuBinding.done.visibility = View.VISIBLE
            }
        })
        mViewModel!!.isOverlay.observe(viewLifecycleOwner, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                overlayBinding.btnAcquireOverlayPermission.setText(R.string.btn_acquired)
                overlayBinding.btnAcquireOverlayPermission.isEnabled = false
                overlayBinding.done.visibility = View.VISIBLE
                mViewModel!!.allPerm.value?.or(InitializeViewModel.OVERLAY_PERM)
                Timber.d("allPerm = %s", mViewModel!!.allPerm.value)
            }
        })
    }

    private fun actCards(card: Int, isAdd: Boolean) {
        when (card) {
            CARD_ROOT -> {
                rootBinding.btnAcquireRootPermission.setOnClickListener {
                    val result = com.absinthe.anywhere_.utils.PermissionUtils.upgradeRootPermission(context?.packageCodePath)
                    mViewModel!!.isRoot.setValue(result)
                }
                if (isAdd) {
                    if (!bRoot) {
                        mBinding.container.addView(rootBinding.root, 1)
                        bRoot = true
                    }
                } else {
                    mBinding.container.removeView(rootBinding.root)
                    bRoot = false
                }
            }
            CARD_SHIZUKU -> {
                shizukuBinding.btnAcquirePermission.isEnabled = false
                shizukuBinding.btnCheckShizukuState.setOnClickListener {
                    val result = ShizukuHelper.checkShizukuOnWorking(context)
                    mViewModel!!.isShizukuCheck.setValue(result)
                }
                shizukuBinding.btnAcquirePermission.setOnClickListener {
                    val result = ShizukuHelper.isGrantShizukuPermission()
                    mViewModel!!.isShizuku.value = result
                    if (!result) {
                        ShizukuHelper.requestShizukuPermission()
                    }
                }
                if (isAdd) {
                    if (!bShizuku) {
                        mBinding.container.addView(shizukuBinding.root, 1)
                        bShizuku = true
                    }
                } else {
                    mBinding.container.removeView(shizukuBinding.root)
                    bShizuku = false
                }
            }
            CARD_OVERLAY -> {
                overlayBinding.btnAcquireOverlayPermission.setOnClickListener {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        mViewModel!!.isOverlay.setValue(true)
                    } else {
                        val isGrant = PermissionUtils.isGrantedDrawOverlays()
                        mViewModel!!.isOverlay.value = isGrant
                        if (!isGrant) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
                            }
                            PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                                override fun onGranted() {
                                    mViewModel!!.isOverlay.value = true
                                }

                                override fun onDenied() {
                                    mViewModel!!.isOverlay.value = false
                                }
                            })
                        }
                    }
                }
                if (isAdd) {
                    if (!bOverlay) {
                        mBinding.container.addView(overlayBinding.root, -1)
                        bOverlay = true
                    }
                } else {
                    mBinding.container.removeView(overlayBinding.root)
                    bOverlay = false
                }
            }
            CARD_POPUP -> {
                popupBinding.btnAcquirePopupPermission.setOnClickListener {
                    if (com.absinthe.anywhere_.utils.PermissionUtils.isMIUI()) {
                        com.absinthe.anywhere_.utils.PermissionUtils.goToMIUIPermissionManager(context)
                    }
                }
                if (isAdd) {
                    if (!bPopup) {
                        mBinding.container.addView(popupBinding.root, -1)
                        bPopup = true
                    }
                } else {
                    mBinding.container.removeView(popupBinding.root)
                    bPopup = false
                }
            }
            else -> {
                popupBinding.btnAcquirePopupPermission.setOnClickListener {
                    if (com.absinthe.anywhere_.utils.PermissionUtils.isMIUI()) {
                        com.absinthe.anywhere_.utils.PermissionUtils.goToMIUIPermissionManager(context)
                    }
                }
                if (isAdd) {
                    if (!bPopup) {
                        mBinding.container.addView(popupBinding.root, -1)
                        bPopup = true
                    }
                } else {
                    mBinding.container.removeView(popupBinding.root)
                    bPopup = false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    mViewModel!!.isOverlay.value = java.lang.Boolean.TRUE
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (ActivityCompat.checkSelfPermission(context!!, ShizukuApiConstants.PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    mViewModel!!.isShizuku.value = java.lang.Boolean.TRUE
                }
            }, 3000)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(context!!, ShizukuApiConstants.PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                mViewModel!!.isShizuku.value = java.lang.Boolean.TRUE
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val CARD_ROOT = 1
        private const val CARD_SHIZUKU = 2
        private const val CARD_OVERLAY = 3
        private const val CARD_POPUP = 4
        private var mViewModel: InitializeViewModel? = null
        @JvmStatic
        fun newInstance(): InitializeFragment {
            return InitializeFragment()
        }
    }
}