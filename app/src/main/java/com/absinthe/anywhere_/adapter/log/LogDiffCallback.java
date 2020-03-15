package com.absinthe.anywhere_.adapter.log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.absinthe.anywhere_.model.LogModel;

public class LogDiffCallback extends DiffUtil.ItemCallback<LogModel> {

    @Override
    public boolean areItemsTheSame(@NonNull LogModel oldItem, @NonNull LogModel newItem) {
        return oldItem.getFilePath().equals(newItem.getFilePath());
    }

    @Override
    public boolean areContentsTheSame(@NonNull LogModel oldItem, @NonNull LogModel newItem) {
        return oldItem.getCreateTime().equals(newItem.getCreateTime())
                && oldItem.getFileSize() == newItem.getFileSize();
    }
}
