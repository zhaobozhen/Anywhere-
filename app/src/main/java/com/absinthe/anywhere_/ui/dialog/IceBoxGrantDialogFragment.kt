package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment

class IceBoxGrantDialogFragment : AnywhereDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AnywhereDialogBuilder(requireContext())
      .setMessage(R.string.dialog_message_ice_box_perm_not_support)
      .setPositiveButton(R.string.dialog_delete_positive_button, null)
      .setNeutralButton(R.string.dialog_go_to_perm_button) { _: DialogInterface?, _: Int ->
        val intent = Intent(Intent.ACTION_VIEW).apply {
          component = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$ManageApplicationsActivity"
          )
        }
        requireActivity().startActivity(intent)
      }
      .create()
  }
}
