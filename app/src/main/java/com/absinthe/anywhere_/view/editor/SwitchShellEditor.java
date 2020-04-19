package com.absinthe.anywhere_.view.editor;

import android.content.Context;

import com.absinthe.anywhere_.R;

public class SwitchShellEditor extends Editor<SwitchShellEditor> {

    public SwitchShellEditor(Context context) {
        super(context, Editor.SWITCH_SHELL);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_switch_shell);
    }

    @Override
    protected void setDoneButton() {

    }

    @Override
    protected void setRunButton() {

    }
}
