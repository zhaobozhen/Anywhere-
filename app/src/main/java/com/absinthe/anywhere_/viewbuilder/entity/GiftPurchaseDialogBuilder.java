package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

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

        LinearLayout.LayoutParams params = Params.LL.customParams(d2p(80), d2p(120));
        params.weight = 1;
        params.setMarginEnd(d2p(20));
        Button btnThirdPrice = new Button(mContext);
        btnThirdPrice.setLayoutParams(params);
        btnThirdPrice.setText("Y14");
        llPriceBox.addView(btnThirdPrice);

        Button btnInfinityPrice = new Button(mContext);
        params.setMarginStart(d2p(20));
        btnInfinityPrice.setLayoutParams(params);
        btnInfinityPrice.setText("Y25");
        llPriceBox.addView(btnInfinityPrice);

        addView(llPriceBox);
    }
}
