package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.utils.QRCodeUtil;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class CardSharingBuilder extends ViewBuilder {

    private String text;

    public CardSharingBuilder(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);

        int padding = d2p(20);
        root.setPadding(padding, padding, padding, padding);

        int size = d2p(250);
        ImageView ivQrCode = new ImageView(mContext);
        ivQrCode.setLayoutParams(Params.LL.customParams(size, size));

        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(text, size, size);
        if (bitmap != null) {
            ivQrCode.setImageBitmap(bitmap);
        }
        addView(ivQrCode);
    }
}
