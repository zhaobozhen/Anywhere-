package com.absinthe.anywhere_.adapter.gift;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseGiftNode extends BaseNode {

    private String msg;
    public int type;

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
