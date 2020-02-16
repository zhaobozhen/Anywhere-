package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.DynamicParamsDialogBuilder;

public class DynamicParamsDialogFragment extends AnywhereDialogFragment {

    private Context mContext;
    private DynamicParamsDialogBuilder mBuilder;
    private OnParamsInputListener mListener;
    private String mText;

    public DynamicParamsDialogFragment(String text) {
        mText = text;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new DynamicParamsDialogBuilder(mContext);
        mBuilder.setParams(mText);

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
    }
}
