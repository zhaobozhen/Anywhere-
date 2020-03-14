package com.absinthe.anywhere_.adapter.log;

import android.text.format.Formatter;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.LogModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class LogAdapter extends BaseQuickAdapter<LogModel, BaseViewHolder> {

    public LogAdapter() {
        super(R.layout.item_card_logcat);
        addChildClickViewIds(R.id.btn_send);
        addChildClickViewIds(R.id.btn_delete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LogModel logModel) {
        baseViewHolder.setText(R.id.tv_create_time, logModel.getCreateTime());
        baseViewHolder.setText(R.id.tv_file_path, logModel.getFilePath());
        baseViewHolder.setText(R.id.tv_file_size, Formatter.formatFileSize(getContext(), logModel.getFileSize()));
    }
}
