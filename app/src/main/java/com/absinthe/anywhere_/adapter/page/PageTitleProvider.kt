package com.absinthe.anywhere_.adapter.page

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class PageTitleProvider : BaseNodeProvider() {

    override val itemViewType: Int
        get() = 1

    override val layoutId: Int
        get() = R.layout.item_page_title

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val node = item as PageTitleNode

        helper.setText(R.id.tv_title, node.title)

        val ivArrow = helper.getView<ImageView>(R.id.iv_arrow)
        if (node.isExpanded) {
            onExpansionToggled(ivArrow, true)
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        if (isEditMode) {
            return
        }
        getAdapter()?.expandOrCollapse(position)
        val node = data as PageTitleNode
        val ivArrow = helper.getView<ImageView>(R.id.iv_arrow)

        if (node.isExpanded) {
            onExpansionToggled(ivArrow, true)
        } else {
            onExpansionToggled(ivArrow, false)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onLongClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int): Boolean {
        if (isEditMode) {
            return false
        }
        val popup = PopupMenu(context, view).apply {
            menuInflater.inflate(R.menu.page_menu, menu)

            if (menu is MenuBuilder) {
                val menuBuilder = menu as MenuBuilder
                menuBuilder.setOptionalIconsVisible(true)
            }
        }

        val node = data as PageTitleNode
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.rename_page -> ActivityStackManager.topActivity?.let {
                    DialogManager.showRenameDialog(it, node.title)
                }
                R.id.delete_page -> DialogManager.showDeletePageDialog(context, node.title, DialogInterface.OnClickListener { _, _ ->
                    getPageEntity(node.title)?.let { AnywhereApplication.sRepository.deletePage(it) }
                    AnywhereApplication.sRepository.allPageEntities.value?.let { list ->
                        val title = list[0].title

                        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
                            for (ae in it) {
                                if (ae.category == node.title) {
                                    ae.category = title
                                    AnywhereApplication.sRepository.update(ae)
                                }
                            }
                        }

                        GlobalValues.setsCategory(title, 0)
                    }
                }, false)
                R.id.delete_page_and_item -> DialogManager.showDeletePageDialog(context, node.title, DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                    getPageEntity(node.title)?.let { AnywhereApplication.sRepository.deletePage(it) }
                    AnywhereApplication.sRepository.allPageEntities.value?.let { list ->
                        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
                            for (ae in it) {
                                if (ae.category == node.title) {
                                    AnywhereApplication.sRepository.delete(ae)
                                }
                            }
                        }

                        GlobalValues.setsCategory(list[0].title, 0)
                    }
                }, true)
            }
            true
        }
        popup.show()
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        return super.onLongClick(helper, view, data, position)
    }

    private fun onExpansionToggled(arrow: ImageView, expanded: Boolean) {
        val start: Float
        val target: Float
        if (expanded) {
            start = 0f
            target = 90f
        } else {
            start = 90f
            target = 0f
        }

        ObjectAnimator.ofFloat(arrow, View.ROTATION, start, target).apply {
            duration = 200
            start()
        }
    }

    companion object {
        @JvmField
        var isEditMode = false

        private fun getPageEntity(title: String): PageEntity? {
            AnywhereApplication.sRepository.allPageEntities.value?.let {
                for (pe in it) {
                    if (pe.title == title) {
                        return pe
                    }
                }
            }
            return null
        }

        @JvmStatic
        fun renameTitle(oldTitle: String, newTitle: String) {
            val pe = getPageEntity(oldTitle)
            AnywhereApplication.sRepository.allAnywhereEntities.value?.let { list ->
                pe?.let {
                    for (ae in list) {
                        if (ae.category == pe.title) {
                            ae.category = newTitle
                            AnywhereApplication.sRepository.update(ae)
                        }
                    }
                    AnywhereApplication.sRepository.deletePage(pe)
                    pe.title = newTitle
                    AnywhereApplication.sRepository.insertPage(pe)
                    GlobalValues.category = newTitle
                }
            }
        }
    }
}