package com.absinthe.anywhere_.view;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.absinthe.anywhere_.utils.manager.DialogStack;

public class AnywhereDialogFragment extends DialogFragment {

    private boolean isDismissParent = false;
    private OnDismissListener mListener;

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

    protected void setWrapOnDismissListener(OnDismissListener listener) {
        mListener = listener;
    }

    protected void setDismissParent(boolean flag) {
        isDismissParent = flag;
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
