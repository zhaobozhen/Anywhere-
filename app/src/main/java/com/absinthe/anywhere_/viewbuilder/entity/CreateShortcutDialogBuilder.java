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
        int padding = dipToPixels(10);
        root.setPadding(padding, padding, padding, padding);
        ((LinearLayout) root).setOrientation(LinearLayout.HORIZONTAL);

        ivIcon = new ImageView(mContext);
        ivIcon.setLayoutParams(new LinearLayout.LayoutParams(
                dipToPixels(45),
                dipToPixels(45)));
        addView(ivIcon);

        etName = new EditText(mContext);
        etName.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        etName.setPadding(padding, 0, padding, 0);
        etName.setSingleLine();
        addView(etName);
    }
}
