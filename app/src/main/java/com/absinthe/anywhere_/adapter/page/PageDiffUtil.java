package com.absinthe.anywhere_.adapter.page;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.chad.library.adapter.base.entity.node.BaseNode;

public class PageDiffUtil extends DiffUtil.ItemCallback<BaseNode> {


    @Override
    public boolean areItemsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        if (oldItem instanceof PageNode && newItem instanceof PageNode) {
            return false;
        } else {
            return ((PageTitleNode) oldItem).getTitle().equals(((PageTitleNode) newItem).getTitle());
        }
    }

    @Override
    public boolean areContentsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        return false;
    }
}
