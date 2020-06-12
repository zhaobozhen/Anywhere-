package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.AdvancedCardSelectDialogBuilder

class AdvancedCardSelectDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: AdvancedCardSelectDialogBuilder
    private var mListener: OnClickItemListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AdvancedCardSelectDialogBuilder(requireContext())

        val builder = AnywhereDialogBuilder(requireContext())
        initView()
        return builder.setView(mBuilder.root)
                .create()
    }

    fun setListener(mListener: OnClickItemListener?) {
        this.mListener = mListener
    }

    private fun initView() {
        mBuilder.tvAddImage.setOnClickListener {
            mListener?.onClick(it, ITEM_ADD_IMAGE)
        }
        mBuilder.tvAddShell.setOnClickListener {
            mListener?.onClick(it, ITEM_ADD_SHELL)
        }
        mBuilder.tvAddSwitchShell.setOnClickListener {
            mListener?.onClick(it, ITEM_ADD_SWITCH_SHELL)
        }
    }

    interface OnClickItemListener {
        fun onClick(view: View, item: Int)
    }

    companion object {
        const val ITEM_ADD_IMAGE = 0
        const val ITEM_ADD_SHELL = 1
        const val ITEM_ADD_SWITCH_SHELL = 2
    }
}