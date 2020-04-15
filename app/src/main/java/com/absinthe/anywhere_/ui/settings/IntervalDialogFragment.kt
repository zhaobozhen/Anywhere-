package com.absinthe.anywhere_.ui.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.IntervalDialogBuilder

class IntervalDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: IntervalDialogBuilder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = IntervalDialogBuilder(requireContext())
        val builder = AnywhereDialogBuilder(requireContext())

        mBuilder.slider.value = GlobalValues.sDumpInterval / 1000f
        return builder.setView(mBuilder.root)
                .setTitle(R.string.dialog_set_interval_title)
                .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
                    val interval = mBuilder.slider.value.toInt() * 1000
                    GlobalValues.setsDumpInterval(interval)
                }
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create()
    }
}