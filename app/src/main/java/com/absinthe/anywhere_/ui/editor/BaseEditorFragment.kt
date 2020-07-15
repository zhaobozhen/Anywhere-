package com.absinthe.anywhere_.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.model.database.AnywhereEntity

abstract class BaseEditorFragment : Fragment() {

    protected lateinit var item: AnywhereEntity
    protected var isEditMode = false

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View
    protected abstract fun initView()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            item = requireArguments().getParcelable(EXTRA_ENTITY)!!
            isEditMode = requireArguments().getBoolean(EXTRA_EDIT_MODE)
        }

        val root = setBinding(inflater, container)
        initView()
        return root
    }
}