package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.BottomSheetDialogUrlSchemeBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.dialog.DynamicParamsDialogFragment.OnParamsInputListener
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.DialogManager.showDynamicParamsDialog
import com.absinthe.anywhere_.utils.manager.URLManager
import jonathanfinerty.once.Once

class SchemeEditorFragment : BaseEditorFragment() {

    private lateinit var binding: BottomSheetDialogUrlSchemeBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = BottomSheetDialogUrlSchemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.btnUrlSchemeCommunity.setOnClickListener {
            if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.SHORTCUT_COMM_TIPS)) {
                DialogManager.showShortcutCommunityTipsDialog(requireActivity(), DialogInterface.OnClickListener { _, _ ->
                    openShortcutCommunity()
                })
                Once.markDone(OnceTag.SHORTCUT_COMM_TIPS)
            } else {
                openShortcutCommunity()
            }
        }
        item?.let {
            binding.tietAppName.setText(it.appName)
            binding.tietUrlScheme.setText(it.param1)
            binding.tietDescription.setText(it.description)

            if (it.param3.isNotBlank()) {
                binding.tietDynamicParams.setText(it.param3)
            }
        }
    }

    override fun tryingRun() {
        val urlScheme = binding.tietUrlScheme.text.toString()
        if (urlScheme.isBlank()) {
            binding.tilUrlScheme.error = getString(R.string.bsd_error_should_not_empty)
            return
        }

        val dynamicParam = binding.tietDynamicParams.text.toString()

        if (dynamicParam.isNotBlank()) {
            showDynamicParamsDialog(requireActivity() as BaseActivity, dynamicParam, object : OnParamsInputListener {
                override fun onFinish(text: String?) {
                    try {
                        URLSchemeHandler.parse(urlScheme + text, requireContext())
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (e is java.lang.RuntimeException) {
                            ToastUtil.makeText(R.string.toast_runtime_error)
                        }
                    }
                }

                override fun onCancel() {}
            })
        } else {
            try {
                URLSchemeHandler.parse(urlScheme, requireContext())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                if (e is ActivityNotFoundException) {
                    ToastUtil.makeText(R.string.toast_no_react_url)
                } else if (e is java.lang.RuntimeException) {
                    ToastUtil.makeText(R.string.toast_runtime_error)
                }
            }
        }
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietUrlScheme.text.isNullOrBlank()) {
            binding.tilUrlScheme.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.tietUrlScheme.text.toString()
            param2 = AppUtils.getPackageNameByScheme(requireContext(), binding.tietUrlScheme.text.toString())
            param3 = binding.tietDynamicParams.text.toString()
            description = binding.tietDescription.text.toString()
        }

        if (isEditMode) {
            if (ae.appName != item!!.appName || ae.param1 != item!!.param1) {
                if (ae.shortcutType == AnywhereType.SHORTCUTS) {
                    if (AppUtils.atLeastNMR1()) {
                        ShortcutsUtils.updateShortcut(ae)
                    }
                }
            }
            AnywhereApplication.sRepository.update(ae)
        } else {
            AnywhereApplication.sRepository.insert(ae)
        }

        return true
    }

    private fun openShortcutCommunity() {
        try {
            URLSchemeHandler.parse(URLManager.SHORTCUT_COMMUNITY_PAGE, requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is ActivityNotFoundException) {
                ToastUtil.makeText(R.string.toast_no_react_url)
            } else if (e is RuntimeException) {
                ToastUtil.makeText(R.string.toast_runtime_error)
            }
        }
    }

    companion object {
        fun newInstance(entity: AnywhereEntity, isEditMode: Boolean): SchemeEditorFragment {
            return SchemeEditorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_ENTITY, entity)
                    putBoolean(EXTRA_EDIT_MODE, isEditMode)
                }
            }
        }
    }
}