package com.absinthe.anywhere_.adapter.page;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class PageDiffUtil extends DiffUtil.ItemCallback<PageTitleNode> {

    @Override
    public Object getChangePayload(@NonNull PageTitleNode oldItem, @NonNull PageTitleNode newItem) {
        return null;
    }

    @Override
    public boolean areItemsTheSame(@NonNull PageTitleNode oldItem, @NonNull PageTitleNode newItem) {
        return oldItem.getTitle().equals(newItem.getTitle());
    }

    @Override
    public boolean areContentsTheSame(@NonNull PageTitleNode oldItem, @NonNull PageTitleNode newItem) {
        return TextUtils.equals(oldItem.getTitle(), newItem.getTitle());
    }
}
