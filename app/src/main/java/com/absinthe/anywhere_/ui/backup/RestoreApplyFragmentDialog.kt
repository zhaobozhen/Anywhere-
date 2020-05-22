package com.absinthe.anywhere_.ui.backup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.google.android.material.textfield.TextInputEditText

class RestoreApplyFragmentDialog : AnywhereDialogFragment() {

    private lateinit var mEditText: TextInputEditText

    val text: String
        get() = mEditText.text.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_restore_apply, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AnywhereDialogBuilder(requireContext())
        val layoutInflater = (requireActivity()).layoutInflater

        @SuppressLint("InflateParams")
        val inflate = layoutInflater.inflate(R.layout.dialog_fragment_restore_apply, null, false)

        mEditText = inflate.findViewById(R.id.tiet_paste)

        val listener = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            StorageUtils.restoreFromJson(requireContext(), text)
        }

        return builder.setView(inflate)
                .setTitle(R.string.settings_backup_apply_title)
                .setPositiveButton(R.string.btn_apply, listener)
                .create()
    }
}