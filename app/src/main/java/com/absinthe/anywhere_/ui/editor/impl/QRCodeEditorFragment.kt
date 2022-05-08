package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorQrCodeBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener

class QRCodeEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorQrCodeBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.tietAppName.setText(item.appName)
        binding.tietDescription.setText(item.description)
    }

    override fun tryRunning() {
        Opener.with(requireContext()).load(item).open()
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        doneItem = AnywhereEntity().apply {
            appName = binding.tietAppName.text.toString()
            param1 = item.param1
            param2 = item.id
            description = binding.tietDescription.text.toString()
            type = AnywhereType.Card.QR_CODE
        }

        if (super.doneEdit()) return true
        if (isEditMode && doneItem == item) return true

        if (isEditMode) {
            if (doneItem.appName != item.appName) {
                if (GlobalValues.shortcutsList.contains(doneItem.id)) {
                    if (AppUtils.atLeastNMR1()) {
                        ShortcutsUtils.updateShortcut(doneItem)
                    }
                }
            }
            AnywhereApplication.sRepository.update(doneItem)
        } else {
            doneItem.id = System.currentTimeMillis().toString()
            AnywhereApplication.sRepository.insert(doneItem)
        }

        return true
    }
}
