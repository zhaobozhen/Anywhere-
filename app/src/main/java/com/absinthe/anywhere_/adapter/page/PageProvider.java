package com.absinthe.anywhere_.adapter.page;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PageProvider extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_page;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        RecyclerView recyclerView = baseViewHolder.getView(R.id.rv_chip);
        ChipAdapter adapter = new ChipAdapter(GlobalValues.sCategory);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

}
