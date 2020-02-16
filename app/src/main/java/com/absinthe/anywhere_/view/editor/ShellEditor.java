package com.absinthe.anywhere_.view.editor;

import android.content.Context;

import com.absinthe.anywhere_.R;

public class ShellEditor extends Editor<ShellEditor> {

    ShellEditor(Context context, int editorType) {
        super(context, Editor.SHELL);
    }

    @Override
    protected void setBottomSheetDialog() {
        setBottomSheetDialogImpl(mContext, R.layout.bottom_sheet_dialog_url_scheme);
    }

    @Override
    protected void setDoneButton() {

    }

    @Override
    protected void setRunButton() {

    }
}
