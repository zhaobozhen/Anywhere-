package com.absinthe.anywhere_.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.model.database.AnywhereEntity

abstract class BaseEditorFragment(val item: AnywhereEntity, val isEditMode: Boolean) : Fragment() {

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