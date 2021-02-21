package com.absinthe.anywhere_.view.app

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.absinthe.anywhere_.utils.manager.DialogStack
import timber.log.Timber

open class AnywhereDialogFragment : DialogFragment() {

    private var isDismissParent = false
    private var mListener: OnDismissListener? = null

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
        DialogStack.push(this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        DialogStack.pop()
        if (isDismissParent) {
            DialogStack.pop()
        }
        mListener?.onDismiss()

    }

    protected fun setWrapOnDismissListener(listener: OnDismissListener?) {
        mListener = listener
    }

    protected fun setDismissParent(flag: Boolean) {
        isDismissParent = flag
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}