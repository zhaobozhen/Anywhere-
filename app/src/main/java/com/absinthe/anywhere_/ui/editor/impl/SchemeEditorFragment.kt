package com.absinthe.anywhere_.ui.editor.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.databinding.BottomSheetDialogUrlSchemeBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY

class SchemeEditorFragment : BaseEditorFragment() {

    private lateinit var binding: BottomSheetDialogUrlSchemeBinding

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = BottomSheetDialogUrlSchemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(entity: AnywhereEntity): SchemeEditorFragment {
            return SchemeEditorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_ENTITY, entity)
                }
            }
        }
    }
}