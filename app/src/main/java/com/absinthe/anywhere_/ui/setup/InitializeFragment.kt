package com.absinthe.anywhere_.ui.setup

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.*
import com.absinthe.anywhere_.ui.main.MainActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.SPUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.ShellManager
import com.absinthe.anywhere_.utils.manager.ShizukuHelper
import com.absinthe.libraries.utils.utils.XiaomiUtilities
import com.blankj.utilcode.util.PermissionUtils
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class InitializeFragment : Fragment(), OnButtonCheckedListener {

    private lateinit var mBinding: FragmentInitializeBinding
    private lateinit var rootBinding: CardAcquireRootPermissionBinding
    private lateinit var shizukuBinding: CardAcquireShizukuPermissionBinding
    private lateinit var overlayBinding: CardAcquireOverlayPermissionBinding
    private lateinit var popupBinding: CardAcquirePopupPermissionBinding

    private var isRoot: MutableLiveData<Boolean> = MutableLiveData()
    private var isOverlay: MutableLiveData<Boolean> = MutableLiveData()
    private var isPopup: MutableLiveData<Boolean> = MutableLiveData()
    private var isShizuku: MutableLiveData<Boolean> = MutableLiveData()
    private var allPerm: MutableLiveData<Int> = MutableLiveData()

    private var bRoot = false
    private var bShizuku = false
    private var bOverlay = false
    private var bPopup = false
    private var hasCheckedShizuku = false
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
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        if (hasCheckedShizuku) {
            if (ShizukuHelper.checkPermission(requireActivity())) {
                isShizuku.value = java.lang.Boolean.TRUE
            }
        }
    }

    private fun initView() {
        setHasOptionsMenu(true)
        mBinding.selectWorkingMode.toggleGroup.addOnButtonCheckedListener(this)
        allPerm.value = 0
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
            GlobalValues.workingMode = mWorkingMode
            SPUtils.putString(requireContext(), Const.PREF_WORKING_MODE, mWorkingMode)

            var flag = false
            val allPerm = allPerm.value ?: 0

            when (mWorkingMode) {
                Const.WORKING_MODE_URL_SCHEME -> flag = true
                Const.WORKING_MODE_ROOT -> flag = if (XiaomiUtilities.isMIUI()) {
                    allPerm == (ROOT_PERM or OVERLAY_PERM or POPUP_PERM)
                } else {
                    allPerm == (ROOT_PERM or OVERLAY_PERM)
                }
                Const.WORKING_MODE_SHIZUKU -> flag = if (XiaomiUtilities.isMIUI()) {
                    allPerm == (SHIZUKU_GROUP_PERM or OVERLAY_PERM or POPUP_PERM)
                } else {
                    allPerm == (SHIZUKU_GROUP_PERM or OVERLAY_PERM)
                }
            }

            if (flag) {
                enterHomePage()
            } else {
                DialogManager.showHasNotGrantPermYetDialog(requireActivity()) {
                    enterHomePage()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enterHomePage() {
        Once.markDone(OnceTag.FIRST_GUIDE)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun initObserver() {
        isRoot.observe(viewLifecycleOwner, { aBoolean: Boolean ->
            if (aBoolean) {
                rootBinding.apply {
                    btnAcquireRootPermission.setText(R.string.btn_acquired)
                    btnAcquireRootPermission.isEnabled = false
                    done.visibility = View.VISIBLE
                }
                allPerm.value = allPerm.value!! or ROOT_PERM
            } else {
                Timber.d("Root permission denied.")
                ToastUtil.makeText(R.string.toast_root_permission_denied)
            }
        })
        isShizuku.observe(viewLifecycleOwner, { aBoolean: Boolean ->
            if (aBoolean) {
                shizukuBinding.apply {
                    btnAcquirePermission.setText(R.string.btn_acquired)
                    btnAcquirePermission.isEnabled = false
                }
                allPerm.value = allPerm.value!! or SHIZUKU_PERM
            }
            allPerm.value?.let {
                if (it and SHIZUKU_GROUP_PERM == SHIZUKU_GROUP_PERM) {
                    shizukuBinding.done.visibility = View.VISIBLE
                }
            }
        })
        isOverlay.observe(viewLifecycleOwner, { aBoolean: Boolean ->
            if (aBoolean) {
                overlayBinding.apply {
                    btnAcquireOverlayPermission.setText(R.string.btn_acquired)
                    btnAcquireOverlayPermission.isEnabled = false
                    done.visibility = View.VISIBLE
                }
                allPerm.value = allPerm.value!! or OVERLAY_PERM
            }
        })
        isPopup.observe(viewLifecycleOwner, { aBoolean: Boolean ->
            if (aBoolean) {
                popupBinding.apply {
                    btnAcquirePopupPermission.setText(R.string.btn_acquired)
                    btnAcquirePopupPermission.isEnabled = false
                    done.visibility = View.VISIBLE
                }
                allPerm.value = allPerm.value!! or POPUP_PERM
            }
        })
    }

    private fun actCards(card: Int, isAdd: Boolean) {
        when (card) {
            CARD_ROOT -> {
                rootBinding.btnAcquireRootPermission.setOnClickListener {
                    isRoot.setValue(ShellManager.acquireRoot())
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
                shizukuBinding.btnAcquirePermission.setOnClickListener {
                    hasCheckedShizuku = true
                    isShizuku.value = ShizukuHelper.checkPermission(requireActivity())
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
                    val isGrant = PermissionUtils.isGrantedDrawOverlays()
                    isOverlay.value = isGrant
                    if (!isGrant) {
                        if (AppUtils.atLeastR()) {
                            ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
                        }
                        PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                            override fun onGranted() {
                                isOverlay.value = true
                            }

                            override fun onDenied() {
                                isOverlay.value = false
                            }
                        })
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
                if (!XiaomiUtilities.isMIUI()) {
                    return
                }
                isPopup.value = XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_BACKGROUND_START_ACTIVITY)
                popupBinding.btnAcquirePopupPermission.setOnClickListener {
                    try {
                        startActivityForResult(XiaomiUtilities.getPermissionManagerIntent(), Const.REQUEST_CODE_GO_TO_MIUI_PERM_MANAGER)
                    } catch (e: ActivityNotFoundException) {
                        Timber.e(e)
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
            else -> return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_GO_TO_MIUI_PERM_MANAGER) {
            isPopup.value = XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_BACKGROUND_START_ACTIVITY)
        } else if (requestCode == Const.REQUEST_CODE_SHIZUKU_PERMISSION) {
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1500)

                withContext(Dispatchers.Main) {
                    if (ShizukuHelper.checkPermission(requireActivity())) {
                        isShizuku.value = java.lang.Boolean.TRUE
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val CARD_ROOT = 1
        private const val CARD_SHIZUKU = 2
        private const val CARD_OVERLAY = 3
        private const val CARD_POPUP = 4

        const val ROOT_PERM = 1.shl(0)
        const val SHIZUKU_CHECK_PERM = 1.shl(1)
        const val SHIZUKU_PERM = 1.shl(2)
        const val OVERLAY_PERM = 1.shl(3)
        const val POPUP_PERM = 1.shl(4)
        const val SHIZUKU_GROUP_PERM = SHIZUKU_PERM or SHIZUKU_CHECK_PERM

        fun newInstance(): InitializeFragment {
            return InitializeFragment()
        }
    }
}