package com.absinthe.anywhere_.adapter.backup

import android.text.format.Formatter
import com.absinthe.anywhere_.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.thegrizzlylabs.sardineandroid.DavResource
import java.text.SimpleDateFormat
import java.util.*

class WebdavRestoreAdapter : BaseQuickAdapter<DavResource, BaseViewHolder>(R.layout.item_webdav_restore) {

    override fun convert(holder: BaseViewHolder, item: DavResource) {
        holder.setText(R.id.tv_title, item.displayName)
        holder.setText(R.id.tv_timestamp, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(item.creation))
        holder.setText(R.id.tv_size, Formatter.formatFileSize(context, item.contentLength))
    }
}