package com.absinthe.anywhere_.view.settings

import android.app.TimePickerDialog
import android.content.Context

class ObservableTimePickerDialog(
        context: Context?,
        listener: OnTimeSetListener?,
        private val mListener: OnCancelledListener,
        hourOfDay: Int, minute: Int, is24HourView: Boolean)
    : TimePickerDialog(context, listener, hourOfDay, minute, is24HourView) {

    override fun cancel() {
        super.cancel()
        mListener.onCancel()
    }

    interface OnCancelledListener {
        fun onCancel()
    }

}