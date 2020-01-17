package com.absinthe.anywhere_.viewbuilder.entity;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.viewbuilder.ViewBuilder;

public class CardListDialogBuilder extends ViewBuilder {

    public AppListAdapter mAdapter;

    public CardListDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void init() {
        root = new ViewFlipper(mContext);
        root.setLayoutParams(Params.LL.MATCH_MATCH);
        int padding = d2p(10);
        root.setPadding(padding, padding, padding, padding);

        RecyclerView rvCardList = new RecyclerView(mContext);

        rvCardList.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        addView(rvCardList);

        rvCardList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AppListAdapter(mContext, AppListAdapter.MODE_CARD_LIST);
        rvCardList.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(AppListAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }
}
