package com.absinthe.anywhere_.view.app

import android.content.Context
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.utils.manager.DialogStack
import com.google.android.material.bottomsheet.BottomSheetDialog

class AnywhereBottomSheetDialog(context: Context) : BottomSheetDialog(context, R.style.BottomSheetDialog) {

    var isPush = false
    private var isDismissParent = false

    override fun show() {
        super.show()

        setOnDismissListener {
            DialogStack.pop()
            if (isDismissParent) {
                DialogStack.pop()
            }
        }
        if (!isPush) {
            DialogStack.push(this)
            isPush = true
        }
    }

    fun setDismissParent(dismissParent: Boolean) {
        isDismissParent = dismissParent
    }
}