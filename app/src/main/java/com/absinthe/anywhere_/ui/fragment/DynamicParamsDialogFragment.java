package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.DynamicParamsDialogBuilder;

public class DynamicParamsDialogFragment extends AnywhereDialogFragment {

    private DynamicParamsDialogBuilder mBuilder;
    private OnParamsInputListener mListener;
    private String mText;

    public DynamicParamsDialogFragment(String text) {
        mText = text;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(getContext());
        mBuilder = new DynamicParamsDialogBuilder(getContext());
        mBuilder.setParams(mText);

        setWrapOnDismissListener(() -> mListener.onCancel());
        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_dynamic_params_title)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) -> {
                    if (mListener != null) {
                        mListener.onFinish(mBuilder.getInputParams());
                    }
                })
                .create();
    }

    public void setListener(OnParamsInputListener mListener) {
        this.mListener = mListener;
    }

    public interface OnParamsInputListener {
        void onFinish(String text);
        void onCancel();
    }
}
