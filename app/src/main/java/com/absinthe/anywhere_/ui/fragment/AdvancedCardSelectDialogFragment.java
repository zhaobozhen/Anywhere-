package com.absinthe.anywhere_.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.AdvancedCardSelectDialogBuilder;

public class AdvancedCardSelectDialogFragment extends AnywhereDialogFragment {

    public static final int ITEM_ADD_IMAGE = 0;
    public static final int ITEM_ADD_SHELL = 1;

    private AdvancedCardSelectDialogBuilder mBuilder;
    private OnClickItemListener mListener;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(getContext());
        mBuilder = new AdvancedCardSelectDialogBuilder(getContext());
        initView();

        return builder.setView(mBuilder.getRoot())
                .create();
    }

    public void setListener(OnClickItemListener mListener) {
        this.mListener = mListener;
    }

    private void initView() {
        mBuilder.tvAddImage.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(ITEM_ADD_IMAGE);
            }
        });
        mBuilder.tvAddShell.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(ITEM_ADD_SHELL);
            }
        });
    }

    public interface OnClickItemListener {
        void onClick(int item);
    }
}
