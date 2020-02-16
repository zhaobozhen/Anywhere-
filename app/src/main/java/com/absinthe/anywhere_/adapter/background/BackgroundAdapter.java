package com.absinthe.anywhere_.adapter.background;

import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackgroundAdapter extends BaseQuickAdapter<BackgroundNode, BaseViewHolder> {

    public BackgroundAdapter() {
        super(R.layout.item_background);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BackgroundNode backgroundNode) {
        Toolbar toolbar = baseViewHolder.findView(R.id.toolbar);
        if (toolbar != null && backgroundNode != null) {
            toolbar.setTitle(backgroundNode.getTitle());
            toolbar.inflateMenu(R.menu.main_menu);
            toolbar.setNavigationIcon(R.drawable.ic_dehaze);

            if (GlobalValues.sIsMd2Toolbar) {
                int marginHorizontal = (int) getContext().getResources().getDimension(R.dimen.toolbar_margin_horizontal);
                int marginVertical = (int) getContext().getResources().getDimension(R.dimen.toolbar_margin_vertical);

                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) toolbar.getLayoutParams();
                newLayoutParams.leftMargin = newLayoutParams.rightMargin = marginHorizontal;
                newLayoutParams.topMargin = newLayoutParams.bottomMargin = marginVertical;
                newLayoutParams.height = UiUtils.d2p(getContext(), 55);
                toolbar.setLayoutParams(newLayoutParams);
                toolbar.setContentInsetStartWithNavigation(0);
                UiUtils.drawMd2Toolbar(getContext(), toolbar, 3);
            }
        }

        ImageView ivBack = baseViewHolder.findView(R.id.iv_background);
        if (ivBack != null && backgroundNode != null) {
            Glide.with(getContext())
                    .load(backgroundNode.getBackground()).centerCrop()
                    .into(ivBack);
        }
    }
}
