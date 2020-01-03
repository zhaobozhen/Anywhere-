package com.absinthe.anywhere_.adapter.page;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PageListAdapter extends BaseNodeAdapter {

    public PageListAdapter() {
        addNodeProvider(new PageTitleProvider());
        addNodeProvider(new PageProvider());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);
        if (node instanceof PageTitleNode) {
            return 1;
        } else if (node instanceof PageNode) {
            return 2;
        }
        return -1;
    }
}
