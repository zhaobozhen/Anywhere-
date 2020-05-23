package com.absinthe.anywhere_.view.app

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.manager.DialogStack
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AnywhereDialogBuilder : MaterialAlertDialogBuilder {

    private var isDismissParent = false //Dismiss this Dialog and its parent Dialog

    constructor(context: Context) : super(context, R.style.AppTheme_Dialog)
    constructor(context: Context, overrideThemeResId: Int) : super(context, overrideThemeResId)

    override fun show(): AlertDialog {
        setOnDismissListener {
            DialogStack.pop()
            if (isDismissParent) {
                DialogStack.pop()
            }
        }

        val dialog = super.show()
        DialogStack.push(dialog)
        return dialog
    }

    fun setDismissParent(flag: Boolean) {
        isDismissParent = flag
    }
}