package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class RenameDialogBuilder extends ViewBuilder {

    public EditText etName;

    public RenameDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_MATCH);
        int padding = d2p(25);
        root.setPadding(padding, padding, padding, padding);
        ((LinearLayout) root).setOrientation(LinearLayout.HORIZONTAL);

        etName = new EditText(mContext);

        LinearLayout.LayoutParams etParam = Params.LL.MATCH_WRAP;
        etParam.setMarginStart(d2p(10));
        etName.setLayoutParams(etParam);

        etName.setSingleLine();
        addView(etName);
    }
}
