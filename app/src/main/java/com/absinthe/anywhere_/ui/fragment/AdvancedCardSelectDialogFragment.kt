package com.absinthe.anywhere_.ui.fragment

import android.app.Dialog
import android.os.Bundle
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.AdvancedCardSelectDialogBuilder

class AdvancedCardSelectDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: AdvancedCardSelectDialogBuilder
    private var mListener: OnClickItemListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AdvancedCardSelectDialogBuilder(requireContext())

        val builder = AnywhereDialogBuilder(context)
        initView()
        return builder.setView(mBuilder.root)
                .create()
    }

    fun setListener(mListener: OnClickItemListener?) {
        this.mListener = mListener
    }

    private fun initView() {
        mBuilder.tvAddImage.setOnClickListener {
            mListener?.onClick(ITEM_ADD_IMAGE)
        }
        mBuilder.tvAddShell.setOnClickListener {
            mListener?.onClick(ITEM_ADD_SHELL)
        }
    }

    interface OnClickItemListener {
        fun onClick(item: Int)
    }

    companion object {
        const val ITEM_ADD_IMAGE = 0
        const val ITEM_ADD_SHELL = 1
    }
}