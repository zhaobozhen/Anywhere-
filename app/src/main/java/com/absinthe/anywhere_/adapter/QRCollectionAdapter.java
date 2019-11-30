package com.absinthe.anywhere_.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.QRCollection;

public class QRCollectionAdapter extends SingleLineStreamCardsAdapter {
    public QRCollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);
        viewHolder.itemView.setOnClickListener(view -> {
            if (items.get(position).getParam1().equals("com.tencent.mm")) {
                QRCollection.Singleton.INSTANCE.getInstance().wechatScan.launch();
            }
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            return false;
        });
    }
}
