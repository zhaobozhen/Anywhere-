package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorFileBinding
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil

class FileEditorFragment  : BaseEditorFragment() {

    private lateinit var binding: EditorFileBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.tilUrl.isEnabled = false

        binding.tietAppName.setText(item.appName)
        binding.tietDescription.setText(item.description)

        binding.btnSelectFile.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                (requireActivity()).startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                ToastUtil.makeText(R.string.toast_no_document_app)
            }
        }

        if (isEditMode) {
            binding.tietUrl.setText(item.param1)
        }
        requireActivity().invalidateOptionsMenu()

        (requireActivity() as BaseActivity).setDocumentResultListener(object : OnDocumentResultListener {
            override fun onResult(uri: Uri) {
                binding.tietUrl.setText(uri.toString())
            }
        })
    }

    override fun tryingRun() {}

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietUrl.text.isNullOrBlank()) {
            binding.tilUrl.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.tietUrl.text.toString()
            description = binding.tietDescription.text.toString()
        }

        if (ae == item) return true

        if (isEditMode) {
            if (ae.appName != item.appName || ae.param1 != item.param1) {
                if (GlobalValues.shortcutsList.contains(ae.id)) {
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.trying_run).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}