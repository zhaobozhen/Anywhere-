package com.absinthe.anywhere_.adapter.tile;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AppListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileCardAdapter extends BaseQuickAdapter<AppListBean, BaseViewHolder> {

    public TileCardAdapter() {
        super(R.layout.card_tile);
        addChildClickViewIds(R.id.btn_select);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable AppListBean appListBean) {
        int position = baseViewHolder.getLayoutPosition();
        switch (position) {
            case 0:
                baseViewHolder.setText(R.id.tv_title, "Tile One");
                break;
            case 1:
                baseViewHolder.setText(R.id.tv_title, "Tile Two");
                break;
            case 2:
                baseViewHolder.setText(R.id.tv_title, "Tile Three");
                break;
            default:
        }

        if (appListBean != null) {
            baseViewHolder.setText(R.id.tv_app_name, appListBean.getAppName());
            baseViewHolder.setText(R.id.tv_param_1, appListBean.getPackageName());
            baseViewHolder.setText(R.id.tv_param_2, appListBean.getClassName());
            baseViewHolder.setImageDrawable(R.id.iv_app_icon, appListBean.getIcon());
        }
    }
}
