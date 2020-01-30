package com.absinthe.anywhere_.adapter.gift;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseNodeAdapter {

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;

    public ChatAdapter() {
        addNodeProvider(new LeftChatProvider());
        addNodeProvider(new RightChatProvider());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);

        if (node instanceof LeftChatNode) {
            return TYPE_LEFT;
        } else if (node instanceof RightChatNode) {
            return TYPE_RIGHT;
        }
        return -1;
    }
}
