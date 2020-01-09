package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class CreateShortcutDialogBuilder extends ViewBuilder {

    public ImageView ivIcon;
    public EditText etName;

    public CreateShortcutDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        int padding = d2p(25);
        root.setPadding(padding, padding, padding, padding);
        ((LinearLayout) root).setOrientation(LinearLayout.HORIZONTAL);

        ivIcon = new ImageView(mContext);
        ivIcon.setLayoutParams(new LinearLayout.LayoutParams(
                d2p(45),
                d2p(45)));
        addView(ivIcon);

        etName = new EditText(mContext);

        LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        etParam.setMarginStart(d2p(10));
        etName.setLayoutParams(etParam);

        etName.setSingleLine();
        addView(etName);
    }
}
