package com.absinthe.anywhere_.adapter.card;

import android.content.Context;
import android.view.HapticFeedbackConstants;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.QRCollection;
import com.absinthe.anywhere_.view.QRCodeEditor;

public class QRCollectionAdapter extends StreamCardsAdapter {
    public QRCollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        AnywhereEntity item = mItems.get(position);
        viewHolder.bind(item);
        viewHolder.itemView.setOnClickListener(view -> {
            switch (mItems.get(position).getId()) {
                case QRCollection.wechatScanId:
                    QRCollection.Singleton.INSTANCE.getInstance().wechatScan.launch();
                    break;
                case QRCollection.wechatPayId:
                    QRCollection.Singleton.INSTANCE.getInstance().wechatPay.launch();
                    break;
                case QRCollection.wechatCollectId:
                    QRCollection.Singleton.INSTANCE.getInstance().wechatCollect.launch();
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
                case QRCollection.alipayCollectId:
                    QRCollection.Singleton.INSTANCE.getInstance().alipayCollect.launch();
                    break;
                case QRCollection.qqScanId:
                    QRCollection.Singleton.INSTANCE.getInstance().qqScan.launch();
                    break;
                case QRCollection.unionpayPayId:
                    QRCollection.Singleton.INSTANCE.getInstance().unionpayPay.launch();
                    break;
                case QRCollection.unionpayCollectId:
                    QRCollection.Singleton.INSTANCE.getInstance().unionpayCollect.launch();
                    break;
                case QRCollection.unionpayScanId:
                    QRCollection.Singleton.INSTANCE.getInstance().unionpayScan.launch();
                    break;
                case QRCollection.unionpaySignInId:
                    QRCollection.Singleton.INSTANCE.getInstance().unionpaySignIn.launch();
                    break;
                default:
            }
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            openEditor(item);
            return true;
        });
    }

    private void openEditor(AnywhereEntity item) {
        mEditor = new QRCodeEditor(mContext)
                .item(item)
                .isEditorMode(false)
                .build();

        mEditor.show();
    }
}
