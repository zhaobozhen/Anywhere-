package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.databinding.EditorA11yBinding
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment

class A11yEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorA11yBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorA11yBinding.inflate(inflater)
        return binding.root
    }

    override fun initView() {
        item.let {
            binding.apply {
                tietAppName.setText(it.appName)
                tietDescription.setText(it.description)
            }
        }
    }

    override fun tryRunning() {

    }

    override fun doneEdit(): Boolean {
        return super.doneEdit()
    }
}