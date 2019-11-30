package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.view.HapticFeedbackConstants;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.view.Editor;

public class QRCollectionAdapter extends StreamCardsAdapter {
    public QRCollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);
        viewHolder.itemView.setOnClickListener(view -> {
            switch (items.get(position).getId()) {
                case QRCollection.wechatScanId:
                    QRCollection.Singleton.INSTANCE.getInstance().wechatScan.launch();
                    break;
                case QRCollection.wechatPayId:
                    QRCollection.Singleton.INSTANCE.getInstance().wechatPay.launch();
                    break;
                case QRCollection.alipayScanId:
                    QRCollection.Singleton.INSTANCE.getInstance().alipayScan.launch();
                    break;
                case QRCollection.alipayPayId:
                    QRCollection.Singleton.INSTANCE.getInstance().alipayPay.launch();
                    break;
                case QRCollection.alipayBusId:
                    QRCollection.Singleton.INSTANCE.getInstance().alipayBus.launch();
                    break;
            }
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            openEditor(item, Editor.QR_CODE, position);
            return true;
        });
    }

    @Override
    void openEditor(AnywhereEntity item, int type, int position) {
        mEditor = new Editor(mContext, type)
                .item(item)
                .isEditorMode(false)
                .build();

        mEditor.show();
    }
}
