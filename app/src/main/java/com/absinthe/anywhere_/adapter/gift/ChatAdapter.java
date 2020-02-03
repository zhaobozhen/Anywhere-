package com.absinthe.anywhere_.adapter.gift;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseNodeAdapter {

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;
    public static final int TYPE_INFO = 2;

    public ChatAdapter() {
        addNodeProvider(new LeftChatProvider());
        addNodeProvider(new RightChatProvider());
        addNodeProvider(new InfoProvider());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);

        if (node instanceof LeftChatNode) {
            return TYPE_LEFT;
        } else if (node instanceof RightChatNode) {
            return TYPE_RIGHT;
        } else if (node instanceof InfoNode) {
            return TYPE_INFO;
        }
        return -1;
    }
}
