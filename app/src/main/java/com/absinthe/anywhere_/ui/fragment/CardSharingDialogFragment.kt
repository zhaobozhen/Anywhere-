package com.absinthe.anywhere_.ui.fragment

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CardSharingBuilder

class CardSharingDialogFragment(private val mText: String) : AnywhereDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AnywhereDialogBuilder(requireContext()).setView(CardSharingBuilder(requireActivity(), mText).root)
                .setTitle(R.string.menu_share_card)
                .setPositiveButton(R.string.dialog_copy) { _: DialogInterface?, _: Int ->
                    val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", mText)

                    cm.setPrimaryClip(mClipData)
                    ToastUtil.makeText(R.string.toast_copied)
                }
                .create()
    }

}