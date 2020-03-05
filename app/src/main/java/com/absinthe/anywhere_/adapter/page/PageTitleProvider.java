package com.absinthe.anywhere_.adapter.page;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.PageEntity;
import com.absinthe.anywhere_.utils.manager.ActivityStackManager;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PageTitleProvider extends BaseNodeProvider {

    public static boolean isEditMode = false;

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
        if (isEditMode) {
            return;
        }

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
        if (isEditMode) {
            return false;
        }

        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater()
                .inflate(R.menu.page_menu, popup.getMenu());
        if (popup.getMenu() instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) popup.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
        }

        PageTitleNode node = (PageTitleNode) data;
        if (node != null) {
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.rename_page:
                        DialogManager.showRenameDialog(ActivityStackManager.getInstance().getTopActivity(), node.getTitle());
                        break;
                    case R.id.delete_page:
                        DialogManager.showDeletePageDialog(getContext(), node.getTitle(), (dialog1, which) -> {
                            AnywhereApplication.sRepository.deletePage(getPageEntity(node.getTitle()));
                            List<PageEntity> list = AnywhereApplication.sRepository.getAllPageEntities().getValue();
                            if (list != null) {
                                String title = list.get(0).getTitle();
                                List<AnywhereEntity> anywhereEntities = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
                                if (anywhereEntities != null) {
                                    for (AnywhereEntity ae : anywhereEntities) {
                                        if (ae.getCategory().equals(node.getTitle())) {
                                            ae.setCategory(title);
                                            AnywhereApplication.sRepository.update(ae);
                                        }
                                    }
                                }
                                GlobalValues.setsCategory(title, 0);
                            }
                        }, false);
                        break;
                    case R.id.delete_page_and_item:
                        DialogManager.showDeletePageDialog(getContext(), node.getTitle(), (dialog1, which) -> {
                            AnywhereApplication.sRepository.deletePage(getPageEntity(node.getTitle()));
                            List<PageEntity> list = AnywhereApplication.sRepository.getAllPageEntities().getValue();
                            if (list != null) {
                                List<AnywhereEntity> anywhereEntities = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
                                if (anywhereEntities != null) {
                                    for (AnywhereEntity ae : anywhereEntities) {
                                        if (ae.getCategory().equals(node.getTitle())) {
                                            AnywhereApplication.sRepository.delete(ae);
                                        }
                                    }
                                }
                                GlobalValues.setsCategory(list.get(0).getTitle(), 0);
                            }
                        }, true);
                        break;
                    default:
                }
                return true;
            });
        }

        popup.show();

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

    private static PageEntity getPageEntity(String title) {
        List<PageEntity> list = AnywhereApplication.sRepository.getAllPageEntities().getValue();
        if (list != null) {
            for (PageEntity pe : list) {
                if (pe.getTitle().equals(title)) {
                    return pe;
                }
            }
        }
        return null;
    }

    public static void renameTitle(String oldTitle, String newTitle) {
        PageEntity pe = getPageEntity(oldTitle);
        List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
        if (list != null && pe != null) {
            for (AnywhereEntity ae : list) {
                if (ae.getCategory().equals(pe.getTitle())) {
                    ae.setCategory(newTitle);
                    AnywhereApplication.sRepository.update(ae);
                }
            }
            AnywhereApplication.sRepository.deletePage(pe);
            pe.setTitle(newTitle);
            AnywhereApplication.sRepository.insertPage(pe);
            GlobalValues.setsCategory(newTitle);
        }
    }
}
