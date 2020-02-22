package com.absinthe.anywhere_.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.manager.DialogStack;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AnywhereBottomSheetFragment extends BottomSheetDialogFragment {

    private boolean isDismissParent = false;
    private AnywhereDialogFragment.OnDismissListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AnywhereBottomSheetDialog(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        DialogStack.push(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        DialogStack.pop();
        if (isDismissParent) {
            DialogStack.pop();
        }
        if (mListener != null) {
            mListener.onDismiss();
        }
    }

    protected void setWrapOnDismissListener(AnywhereDialogFragment.OnDismissListener listener) {
        mListener = listener;
    }

    public void setDismissParent(boolean flag) {
        isDismissParent = flag;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
