package com.absinthe.anywhere_.adapter.page;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PageTitleProvider extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_page_title;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, @Nullable BaseNode baseNode) {
        PageTitleNode node = (PageTitleNode) baseNode;
        if (node != null) {
            baseViewHolder.setText(R.id.tv_title, node.getTitle());
            ImageView ivArrow = baseViewHolder.getView(R.id.iv_arrow);
            if (node.isExpanded()) {
                onExpansionToggled(ivArrow, true);
            }
        }

    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        Objects.requireNonNull(
                getAdapter()).expandOrCollapse(position);

        PageTitleNode node = (PageTitleNode) data;
        if (node != null) {
            ImageView ivArrow = helper.getView(R.id.iv_arrow);
            if (node.isExpanded()) {
                onExpansionToggled(ivArrow, true);
            } else {
                onExpansionToggled(ivArrow, false);
            }
        }
    }

    @Override
    public boolean onLongClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        PageTitleNode node = (PageTitleNode) data;
        MainActivity.getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .replace(R.id.container, MainFragment.newInstance(node.getTitle()))
                .commitNow();
        MainActivity.getInstance().mDrawer.closeDrawer(GravityCompat.START);
        GlobalValues.setsCategory(node.getTitle(), position);
        return super.onLongClick(helper, view, data, position);
    }

    private void onExpansionToggled(ImageView arrow, boolean expanded) {
        float start, target;
        if (expanded) {
            start = 0f;
            target = 90f;
        } else {
            start = 90f;
            target = 0f;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrow, View.ROTATION, start, target);
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }
}
