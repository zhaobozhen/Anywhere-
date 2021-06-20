package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorSwitchShellBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils

const val SWITCH_OFF = "off"
const val SWITCH_ON = "on"

class SwitchShellEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorSwitchShellBinding
    override var execWithRoot: Boolean = false

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorSwitchShellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.tietAppName.setText(item.appName)
        binding.tietDescription.setText(item.description)
        binding.tietSwitchOn.setText(item.param1)
        binding.tietSwitchOff.setText(item.param2)
    }

    override fun tryRunning() {}

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietSwitchOn.text.isNullOrBlank()) {
            binding.tilSwitchOn.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietSwitchOff.text.isNullOrBlank()) {
            binding.tilSwitchOff.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        doneItem = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.tietSwitchOn.text.toString()
            param2 = binding.tietSwitchOff.text.toString()
            param3 = SWITCH_OFF
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
            doneItem.id = System.currentTimeMillis().toString()
            AnywhereApplication.sRepository.insert(doneItem)
        }

        return true
    }
}