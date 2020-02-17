package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class AdvancedCardSelectDialogBuilder extends ViewBuilder {

    public TextView tvAddImage, tvAddShell;

    public AdvancedCardSelectDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);
        ((LinearLayout) root).setOrientation(LinearLayout.VERTICAL);

        tvAddImage = addItem(R.string.btn_add_image, R.drawable.ic_photo);
        tvAddShell = addItem(R.string.btn_add_shell, R.drawable.ic_code);
    }

    private TextView addItem(int titleRes, int iconRes) {
        int padding = d2p(15);
        TextView itemView = new TextView(mContext);
        itemView.setLayoutParams(Params.LL.MATCH_WRAP);
        itemView.setPadding(padding, padding, padding, padding);
        itemView.setText(titleRes);
        itemView.setTextSize(20);
        itemView.setTextColor(mResources.getColor(R.color.textColorNormal));
        itemView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(mContext, iconRes), null, null, null);
        itemView.setCompoundDrawablePadding(padding);
        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        itemView.setBackgroundResource(outValue.resourceId);
        itemView.setClickable(true);
        addView(itemView);

        return itemView;
    }
}
