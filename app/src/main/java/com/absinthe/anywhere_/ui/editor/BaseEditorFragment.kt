package com.absinthe.anywhere_.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.model.database.AnywhereEntity

abstract class BaseEditorFragment : Fragment() {

    val item by lazy { arguments?.getParcelable(EXTRA_ENTITY) as AnywhereEntity? }
    val isEditMode by lazy { arguments?.getBoolean(EXTRA_EDIT_MODE) ?: false }

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View
    protected abstract fun initView()

    abstract fun tryingRun()
    abstract fun doneEdit(): Boolean

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = setBinding(inflater, container)
        initView()
        return root
    }

}