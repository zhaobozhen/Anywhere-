package com.absinthe.anywhere_.adapter.log

import androidx.recyclerview.widget.DiffUtil
import com.absinthe.anywhere_.model.viewholder.LogModel

class LogDiffCallback : DiffUtil.ItemCallback<LogModel>() {

    override fun areItemsTheSame(oldItem: LogModel, newItem: LogModel): Boolean {
        return oldItem.filePath == newItem.filePath
    }

    override fun areContentsTheSame(oldItem: LogModel, newItem: LogModel): Boolean {
        return oldItem.createTime == newItem.createTime
                && oldItem.fileSize == newItem.fileSize
    }
}