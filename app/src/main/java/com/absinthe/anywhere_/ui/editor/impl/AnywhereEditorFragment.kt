package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.databinding.EditorAnywhereBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener

class AnywhereEditorFragment(item: AnywhereEntity, isEditMode: Boolean) : BaseEditorFragment(item, isEditMode) {

    private lateinit var binding: EditorAnywhereBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorAnywhereBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        item.let {
            binding.tietAppName.setText(it.appName)
            binding.tietPackageName.setText(it.param1)
            binding.tietClassName.setText(it.param2)
            binding.tietIntentExtra.setText(it.param3)
            binding.tietDescription.setText(it.description)
        }
    }

    override fun tryingRun() {
        if (binding.tietPackageName.text.isNullOrBlank()) {
            binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
            return
        }
        if (binding.tietClassName.text.isNullOrBlank()) {
            binding.tilClassName.error = getString(R.string.bsd_error_should_not_empty)
            return
        }

        val ae = AnywhereEntity(item).apply {
            param1 = binding.tietPackageName.text.toString()
            param2 = binding.tietClassName.text.toString()
            param3 = binding.tietIntentExtra.text.toString()
        }
        Opener.with(requireContext()).load(ae).open()
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietPackageName.text.isNullOrBlank()) {
            binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietClassName.text.isNullOrBlank()) {
            binding.tilClassName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.tietPackageName.text.toString()
            param2 = binding.tietClassName.text.toString()
            param3 = binding.tietIntentExtra.text.toString()
            description = binding.tietDescription.text.toString()
        }

        if (ae == item) return true

        if (isEditMode) {
            if (ae.appName != item.appName || ae.param1 != item.param1) {
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
}