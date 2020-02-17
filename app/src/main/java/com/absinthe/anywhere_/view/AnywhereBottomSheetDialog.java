package com.absinthe.anywhere_.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.manager.DialogStack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AnywhereBottomSheetDialog extends BottomSheetDialog {

    public boolean isPush = false;
    private boolean isDismissParent = false;

    public AnywhereBottomSheetDialog(@NonNull Context context) {
        super(context, R.style.CustomBottomSheetDialog);
    }

    @Override
    public void show() {
        super.show();
        setOnDismissListener(dialog -> {
            DialogStack.pop();
            if (isDismissParent) {
                DialogStack.pop();
            }
        });
        if (!isPush) {
            DialogStack.push(this);
            isPush = true;
        }
    }

    public void setDismissParent(boolean dismissParent) {
        isDismissParent = dismissParent;
    }
}
