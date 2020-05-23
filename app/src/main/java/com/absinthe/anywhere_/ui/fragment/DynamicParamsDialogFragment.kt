package com.absinthe.anywhere_.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.DynamicParamsDialogBuilder

class DynamicParamsDialogFragment(private val mText: String) : AnywhereDialogFragment() {

    private lateinit var mBuilder: DynamicParamsDialogBuilder
    private var mListener: OnParamsInputListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = DynamicParamsDialogBuilder(requireContext())
        mBuilder.setParams(mText)

        val builder = AnywhereDialogBuilder(requireContext())
        setWrapOnDismissListener(object : OnDismissListener {
            override fun onDismiss() {
                mListener?.onCancel()
            }
        })

        return builder.setView(mBuilder.root)
                .setTitle(R.string.dialog_dynamic_params_title)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    mListener?.onFinish(mBuilder.inputParams)
                }
                .create()
    }

    fun setListener(mListener: OnParamsInputListener?) {
        this.mListener = mListener
    }

    interface OnParamsInputListener {
        fun onFinish(text: String?)
        fun onCancel()
    }

}