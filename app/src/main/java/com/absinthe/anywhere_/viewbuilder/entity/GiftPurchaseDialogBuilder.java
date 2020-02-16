package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class GiftPurchaseDialogBuilder extends ViewBuilder {

    public GiftPurchaseDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);

        LinearLayout llPriceBox = new LinearLayout(mContext);
        llPriceBox.setLayoutParams(Params.LL.MATCH_WRAP);
        llPriceBox.setOrientation(LinearLayout.HORIZONTAL);
        int padding = (int) mResources.getDimension(R.dimen.bsd_edit_text_margin_horizontal);
        llPriceBox.setPadding(padding, padding, padding, padding);

        TextView btnThirdPrice = new TextView(mContext);
        initPriceView(btnThirdPrice, "Y14\n单设备\n激活三次");
        llPriceBox.addView(btnThirdPrice);

        TextView btnInfinityPrice = new TextView(mContext);
        initPriceView(btnInfinityPrice, "Y30\n单设备\n激活无限次");
        llPriceBox.addView(btnInfinityPrice);

        addView(llPriceBox);
    }

    private void initPriceView(TextView view, String text) {
        LinearLayout.LayoutParams params = Params.LL.customParams(d2p(80), d2p(120));
        params.weight = 1;
        params.setMarginEnd(d2p(20));

        view.setLayoutParams(params);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundResource(R.drawable.bg_price);
    }
}
