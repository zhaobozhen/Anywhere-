package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.page.PageTitleProvider.Companion.renameTitle
import com.absinthe.anywhere_.utils.doOnMainThreadIdle
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.RenameDialogBuilder

class RenameDialogFragment : AnywhereDialogFragment() {

  private val title by lazy { arguments?.getString(EXTRA_SHARING_TEXT).orEmpty() }
  private lateinit var mBuilder: RenameDialogBuilder

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    mBuilder = RenameDialogBuilder(requireContext()).apply {
      etName.apply {
        setText(title)
        requestFocus()

        doOnMainThreadIdle({
          val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
          inputManager.showSoftInput(this, 0)
        })
      }
    }

    return AnywhereDialogBuilder(requireContext()).setView(mBuilder.root)
      .setTitle(R.string.dialog_rename_title)
      .setPositiveButton(R.string.dialog_delete_positive_button) { _: DialogInterface?, _: Int ->
        renameTitle(
          title,
          mBuilder.etName.text.toString()
        )
      }
      .setNegativeButton(android.R.string.cancel, null)
      .create()
  }

  val text: String
    get() = mBuilder.etName.text.toString()

}
