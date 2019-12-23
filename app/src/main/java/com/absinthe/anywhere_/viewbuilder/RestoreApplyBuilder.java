package com.absinthe.anywhere_.viewbuilder;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RestoreApplyBuilder extends ViewBuilder {

    public TextInputEditText editText;

    public RestoreApplyBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        TextInputLayout textInputLayout = new TextInputLayout(mContext);
        LinearLayout.LayoutParams tilParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        Resources resources = mContext.getResources();
        tilParams.setMargins(
                dipToPixels(resources.getDimension(R.dimen.bsd_edit_text_margin_horizontal)),
                dipToPixels(resources.getDimension(R.dimen.bsd_item_margin_vertical)),
                dipToPixels(resources.getDimension(R.dimen.bsd_edit_text_margin_horizontal)),
                0);
        textInputLayout.setLayoutParams(tilParams);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setHint(resources.getString(R.string.dialog_restore_apply_paste_hint));

        editText = new TextInputEditText(mContext);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textInputLayout.addView(editText);

        addView(textInputLayout);
    }
}
