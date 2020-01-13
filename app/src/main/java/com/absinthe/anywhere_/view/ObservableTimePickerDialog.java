package com.absinthe.anywhere_.view;

import android.app.TimePickerDialog;
import android.content.Context;

public class ObservableTimePickerDialog extends TimePickerDialog {

    private OnCancelledListener mListener;

    public ObservableTimePickerDialog(Context context, OnTimeSetListener listener, OnCancelledListener cancelListener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
        mListener = cancelListener;
    }

    @Override
    public void cancel() {
        super.cancel();
        mListener.onCancel();
    }

    public interface OnCancelledListener {
        void onCancel();
    }
}
