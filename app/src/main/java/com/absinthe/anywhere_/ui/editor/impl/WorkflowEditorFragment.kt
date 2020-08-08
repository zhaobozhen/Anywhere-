package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.databinding.EditorAnywhereBinding
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.IEditor

class WorkflowEditorFragment  : BaseEditorFragment(), IEditor {

    private lateinit var binding: EditorAnywhereBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorAnywhereBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
    }

    override fun tryRunning() {
    }

    override fun doneEdit(): Boolean {
        return true
    }
}