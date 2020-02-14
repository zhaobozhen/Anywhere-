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
        viewHolder.itemView.setOnClickListener(view ->
                QRCollection.Singleton.INSTANCE.getInstance().getQREntity(mItems.get(position).getId()).launch());

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
