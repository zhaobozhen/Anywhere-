package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.databinding.EditorShellBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CommandUtils.execAdbCmd
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.manager.DialogManager.showShellResultDialog

class ShellEditorFragment :BaseEditorFragment() {

    private lateinit var binding: EditorShellBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorShellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.tietAppName.setText(item.appName)
        binding.tietDescription.setText(item.description)
        binding.etShellContent.setText(item.param1)
    }

    override fun tryingRun() {
        if (binding.etShellContent.text.isNullOrBlank()) {
            binding.etShellContent.error = getString(R.string.bsd_error_should_not_empty)
            return
        }

        val result = execAdbCmd(binding.etShellContent.text.toString())
        showShellResultDialog(requireContext(), result, null, null)
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.etShellContent.text.isNullOrBlank()) {
            binding.etShellContent.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.etShellContent.text.toString()
            description = binding.tietDescription.text.toString()
        }
        
        if (ae == item) return true

        if (isEditMode) {
            if (ae.appName != item.appName || ae.param1 != item.param1) {
                if (ae.shortcutType == AnywhereType.Property.SHORTCUTS) {
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
}