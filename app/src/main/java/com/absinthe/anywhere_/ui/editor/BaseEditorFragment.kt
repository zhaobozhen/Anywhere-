package com.absinthe.anywhere_.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.databinding.FragmentCategoryCardBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity

abstract class BaseEditorFragment : Fragment() {

    val item by lazy { arguments?.getParcelable(EXTRA_ENTITY) as AnywhereEntity? }

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View

    protected fun initView(root: View): View {
        return root
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(setBinding(inflater, container))
    }

}