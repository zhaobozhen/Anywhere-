package com.absinthe.anywhere_.adapter.gift;

import com.absinthe.anywhere_.R;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeftChatProvider extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return ChatAdapter.TYPE_LEFT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_left_chat;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        if (baseNode != null) {
            String msg = ((LeftChatNode) baseNode).getMsg();
            baseViewHolder.setText(R.id.tv_message, msg);
        }
    }
}
