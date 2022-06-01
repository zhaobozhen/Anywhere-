package com.absinthe.anywhere_.ui.dialog

import android.content.DialogInterface
import com.absinthe.anywhere_.view.card.ShellResultContentView
import com.absinthe.libraries.utils.base.BaseBottomSheetViewDialogFragment
import com.absinthe.libraries.utils.view.BottomSheetHeaderView

const val EXTRA_CONTENT = "EXTRA_CONTENT"
const val EXTRA_NEED_FINISH_ACTIVITY = "EXTRA_NEED_FINISH_ACTIVITY"

class ShellResultBottomSheetDialogFragment : BaseBottomSheetViewDialogFragment<ShellResultContentView>() {

  private val content by lazy { arguments?.getString(EXTRA_CONTENT) }
  private val needFinishActivity by lazy { arguments?.getBoolean(EXTRA_NEED_FINISH_ACTIVITY, false) ?: false }

  override fun initRootView(): ShellResultContentView = ShellResultContentView(requireContext())

  override fun getHeaderView(): BottomSheetHeaderView = root.getHeaderView()

  override fun init() {
    root.content.text = content
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)

    if (needFinishActivity) {
      activity?.finish()
    }
  }
}
