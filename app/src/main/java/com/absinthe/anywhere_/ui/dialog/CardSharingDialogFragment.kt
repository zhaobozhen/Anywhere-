package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CardSharingBuilder

const val EXTRA_SHARING_TEXT = "EXTRA_SHARING_TEXT"

class CardSharingDialogFragment : AnywhereDialogFragment() {

  private val text by lazy { arguments?.getString(EXTRA_SHARING_TEXT).orEmpty() }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AnywhereDialogBuilder(requireContext()).setView(
      CardSharingBuilder(
        requireActivity(),
        text
      ).root
    )
      .setTitle(R.string.menu_share_card)
      .setPositiveButton(R.string.dialog_copy) { _: DialogInterface?, _: Int ->
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", text)
        cm.setPrimaryClip(mClipData)
        GlobalValues.shouldListenClipBoard = false
        ToastUtil.makeText(R.string.toast_copied)
      }
      .create()
  }

}
