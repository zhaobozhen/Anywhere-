package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;
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
        root.setLayoutParams(Params.LL.MATCH_MATCH);

        TextInputLayout textInputLayout = new TextInputLayout(mContext);
        LinearLayout.LayoutParams tilParams = Params.LL.MATCH_WRAP;
        Resources resources = mContext.getResources();
        tilParams.setMargins(
                d2p(resources.getDimension(R.dimen.bsd_edit_text_margin_horizontal)),
                d2p(resources.getDimension(R.dimen.bsd_item_margin_vertical)),
                d2p(resources.getDimension(R.dimen.bsd_edit_text_margin_horizontal)),
                0);
        textInputLayout.setLayoutParams(tilParams);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setHint(resources.getString(R.string.dialog_restore_apply_paste_hint));

        editText = new TextInputEditText(mContext);
        editText.setLayoutParams(Params.LL.MATCH_WRAP);
        textInputLayout.addView(editText);

        addView(textInputLayout);
    }
}
