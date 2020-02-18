package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        ((LinearLayout) root).setOrientation(LinearLayout.HORIZONTAL);

        tvAddImage = addItem(R.string.btn_add_image, R.drawable.ic_photo);
        tvAddShell = addItem(R.string.btn_add_shell, R.drawable.ic_code);
    }

    private TextView addItem(int titleRes, int iconRes) {
        int padding = d2p(15);
        TextView itemView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        itemView.setLayoutParams(params);
        itemView.setPadding(padding, padding, padding, padding);
        itemView.setText(titleRes);
        itemView.setTextSize(15);
        itemView.setGravity(Gravity.CENTER);
        itemView.setTextColor(mResources.getColor(R.color.textColorNormal));
        itemView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, iconRes, 0, 0);
        itemView.setCompoundDrawablePadding(d2p(5));
        itemView.setTypeface(null, Typeface.BOLD);

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        itemView.setBackgroundResource(outValue.resourceId);
        itemView.setClickable(true);
        addView(itemView);

        return itemView;
    }
}
