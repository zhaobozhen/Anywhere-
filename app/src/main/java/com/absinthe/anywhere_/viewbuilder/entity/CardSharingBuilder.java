package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.utils.QRCodeUtil;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class CardSharingBuilder extends ViewBuilder {

    private String text;
    private ImageView ivQrCode;

    public CardSharingBuilder(Context context, String text) {
        super(context);
        this.text = text;
        initView();
    }

    @Override
    public void init() {
        root = new LinearLayout(mContext);
        root.setLayoutParams(Params.LL.MATCH_WRAP);

        int padding = d2p(20);
        root.setPadding(padding, padding, padding, padding);
        ((LinearLayout) root).setGravity(Gravity.CENTER);

        int size = d2p(250);
        ivQrCode = new ImageView(mContext);
        ivQrCode.setLayoutParams(Params.LL.customParams(size, size));

        addView(ivQrCode);
    }

    private void initView() {
        int size = d2p(250);
        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(text, size, size);
        if (bitmap != null) {
            ivQrCode.setImageBitmap(bitmap);
        }
    }
}
