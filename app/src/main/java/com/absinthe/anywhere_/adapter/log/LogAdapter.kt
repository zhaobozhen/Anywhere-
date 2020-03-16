package com.absinthe.anywhere_.adapter.log

import android.text.format.Formatter
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.LogModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LogAdapter : BaseQuickAdapter<LogModel, BaseViewHolder>(R.layout.item_card_logcat) {

    override fun convert(helper: BaseViewHolder, item: LogModel) {
        helper.setText(R.id.tv_create_time, item.createTime)
        helper.setText(R.id.tv_file_path, item.filePath)
        helper.setText(R.id.tv_file_size, Formatter.formatFileSize(context, item.fileSize))
    }

    init {
        addChildClickViewIds(R.id.btn_send)
        addChildClickViewIds(R.id.btn_delete)
    }
}