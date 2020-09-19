package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorShellBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener

class ShellEditorFragment : BaseEditorFragment() {

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

    override fun tryRunning() {
        if (binding.etShellContent.text.isNullOrBlank()) {
            binding.etShellContent.error = getString(R.string.bsd_error_should_not_empty)
            return
        }

        val doneItem = AnywhereEntity(item).apply {
            param1 = binding.etShellContent.text.toString()
        }
        Opener.with(requireContext()).load(doneItem).open()
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

        doneItem = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.etShellContent.text.toString()
            description = binding.tietDescription.text.toString()
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
            AnywhereApplication.sRepository.insert(doneItem)
        }

        return true
    }
}