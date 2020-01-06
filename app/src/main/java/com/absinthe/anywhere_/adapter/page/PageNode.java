package com.absinthe.anywhere_.adapter.page;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PageNode extends BaseExpandNode {

    private String title;

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
