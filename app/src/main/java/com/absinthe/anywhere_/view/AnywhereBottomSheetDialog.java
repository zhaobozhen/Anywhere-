package com.absinthe.anywhere_.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.utils.manager.DialogStack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AnywhereBottomSheetDialog extends BottomSheetDialog {

    public AnywhereBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        setOnDismissListener(dialog -> DialogStack.pop());
        DialogStack.push(this);
    }
}
