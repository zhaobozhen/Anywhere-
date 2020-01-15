package com.absinthe.anywhere_.adapter.page;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.R;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        String title = ((PageNode) Objects.requireNonNull(baseNode)).getTitle();

        RecyclerView recyclerView = baseViewHolder.getView(R.id.rv_chip);
        ChipAdapter adapter = new ChipAdapter(title);
        if (adapter.getItemCount() == 0) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        } else if (adapter.getItemCount() <= 3) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(adapter.getItemCount(), StaggeredGridLayoutManager.HORIZONTAL));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        }
        recyclerView.setAdapter(adapter);
    }
}
