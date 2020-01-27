package com.absinthe.anywhere_.adapter.page;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.ItemTouchCallBack;
import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PageListAdapter extends BaseNodeAdapter implements ItemTouchCallBack.OnItemTouchListener {

    public PageListAdapter() {
        addNodeProvider(new PageTitleProvider());
        addNodeProvider(new PageProvider());
        addChildClickViewIds(R.id.iv_entry);
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

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getData(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getData(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {

    }
}
