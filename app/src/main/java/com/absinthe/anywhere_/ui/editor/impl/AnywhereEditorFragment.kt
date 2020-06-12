package com.absinthe.anywhere_.ui.editor.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.databinding.BottomSheetDialogAnywhereBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY

class AnywhereEditorFragment : BaseEditorFragment() {

    private lateinit var binding: BottomSheetDialogAnywhereBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = BottomSheetDialogAnywhereBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {

    }

    override fun tryingRun() {

    }

    override fun doneEdit(): Boolean {
        return false
    }

    companion object {
        fun newInstance(entity: AnywhereEntity, isEditMode: Boolean): AnywhereEditorFragment {
            return AnywhereEditorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_ENTITY, entity)
                    putBoolean(EXTRA_EDIT_MODE, isEditMode)
                }
            }
        }
    }
}