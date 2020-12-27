package com.absinthe.anywhere_.adapter.card

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.dialog.EXTRA_FROM_WORKFLOW
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.utils.AppUtils.isAppFrozen
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.view.card.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.catchingnow.icebox.sdk_client.IceBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.card.MaterialCardView

const val ADAPTER_MODE_NORMAL = 0
const val ADAPTER_MODE_SORT = 1
const val ADAPTER_MODE_SELECT = 2

const val LAYOUT_MODE_LARGE = 0
const val LAYOUT_MODE_MEDIUM = 1
const val LAYOUT_MODE_SMALL = 2
const val LAYOUT_MODE_MINIMUM = 3

const val SNOW_FLAKE_EMOJI = "\u2744"

class BaseCardAdapter(private val layoutMode: Int) : BaseQuickAdapter<AnywhereEntity, BaseViewHolder>(0), ItemTouchCallBack.OnItemTouchListener {

    var mode = ADAPTER_MODE_NORMAL
    private val selectedIndex = mutableListOf<Int>()

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (layoutMode) {
            LAYOUT_MODE_LARGE -> createBaseViewHolder(CardItemView(context, NormalItemView(context)))
            LAYOUT_MODE_MEDIUM -> createBaseViewHolder(CardItemView(context, StreamItemView(context)))
            LAYOUT_MODE_SMALL -> createBaseViewHolder(CardItemView(context, StreamSingleLineItemView(context)))
            LAYOUT_MODE_MINIMUM -> createBaseViewHolder(CardItemView(context, MinimumItemView(context)))
            else -> createBaseViewHolder(CardItemView(context, NormalItemView(context)))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(holder: BaseViewHolder, item: AnywhereEntity) {

        val itemView = holder.itemView as CardItemView<*>

        itemView.appName.text = try {
            if (IceBox.getAppEnabledSetting(context, item.packageName) != 0) {
                SNOW_FLAKE_EMOJI + item.appName
            } else {
                item.appName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            item.appName
        }

        when (layoutMode) {
            LAYOUT_MODE_LARGE -> {
                val normalView = itemView as CardItemView<NormalItemView>

                normalView.content.description.isGone = item.description.isEmpty()
                normalView.content.param1.isGone = item.type == AnywhereType.Card.QR_CODE
                        || item.type == AnywhereType.Card.IMAGE
                        || item.type == AnywhereType.Card.BROADCAST
                        || item.type == AnywhereType.Card.WORKFLOW
                        || item.type == AnywhereType.Card.ACCESSIBILITY
                normalView.content.param2.isGone = item.type == AnywhereType.Card.URL_SCHEME
                        || item.type == AnywhereType.Card.QR_CODE
                        || item.type == AnywhereType.Card.IMAGE
                        || item.type == AnywhereType.Card.BROADCAST
                        || item.type == AnywhereType.Card.WORKFLOW

                normalView.content.description.text = item.description
                normalView.content.param1.text = item.param1
                normalView.content.param2.text = item.param2
            }
            LAYOUT_MODE_MEDIUM, LAYOUT_MODE_SMALL -> {
                val normalView: CardItemView<StreamItemView>? = if (layoutMode == LAYOUT_MODE_MEDIUM) {
                    itemView as CardItemView<StreamItemView>
                } else {
                    null
                }

                if (layoutMode == LAYOUT_MODE_MEDIUM) {
                    normalView!!.content.description.text = item.description
                }

                if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_PURE) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground, UxUtils.getAppIcon(context, item)) { color ->
                            if (color != 0) {
                                itemView.rootView.backgroundTintList = ColorStateList.valueOf(color)
                                itemView.appName.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                                item.color = color

                                if (shouldUpdateColorInfo(context, item)) {
                                    AnywhereApplication.sRepository.update(item)
                                } else {
                                    itemView.rootView.backgroundTintList = ColorStateList.valueOf(color)
                                    itemView.appName.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                    normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                }
                            } else {
                                itemView.appName.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                                normalView?.content?.description?.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                            }
                        }
                    } else {
                        itemView.rootView.backgroundTintList = ColorStateList.valueOf(item.color)
                        itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                        normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                    }
                } else if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_GRADIENT) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground, UxUtils.getAppIcon(context, item)) { color ->
                            item.color = color
                            if (shouldUpdateColorInfo(context, item)) {
                                AnywhereApplication.sRepository.update(item)
                            } else {
                                itemView.cardBackground.post {
                                    UxUtils.createLinearGradientBitmap(context, itemView.cardBackground, color)
                                    itemView.appName.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                    normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                }
                            }
                        }
                        itemView.cardBackground.setImageDrawable(null)
                    } else {
                        itemView.cardBackground.post {
                            UxUtils.createLinearGradientBitmap(context, itemView.cardBackground, item.color)
                            itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                            normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                        }
                    }
                } else {
                    itemView.cardBackground.setImageDrawable(null)
                }
            }
            LAYOUT_MODE_MINIMUM -> {
                if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_PURE) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground, UxUtils.getAppIcon(context, item)) { color ->
                            if (color != 0) {
                                itemView.rootView.backgroundTintList = ColorStateList.valueOf(color)
                                itemView.appName.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                item.color = color

                                if (shouldUpdateColorInfo(context, item)) {
                                    AnywhereApplication.sRepository.update(item)
                                }
                            } else {
                                itemView.appName.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                            }
                        }
                    } else {
                        itemView.rootView.backgroundTintList = ColorStateList.valueOf(item.color)
                        itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                    }
                } else if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_GRADIENT) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground, UxUtils.getAppIcon(context, item)) { color ->
                            item.color = color
                            if (shouldUpdateColorInfo(context, item)) {
                                AnywhereApplication.sRepository.update(item)
                            }

                            if (color == 0) {
                                itemView.cardBackground.setImageDrawable(null)
                            } else {
                                itemView.cardBackground.post {
                                    UxUtils.createLinearGradientBitmap(context, itemView.cardBackground, item.color)
                                    itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                                }
                            }
                        }
                    } else {
                        itemView.cardBackground.post {
                            UxUtils.createLinearGradientBitmap(context, itemView.cardBackground, item.color)
                            itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                        }
                    }
                } else {
                    itemView.cardBackground.setImageDrawable(null)
                }
            }
        }

        if (item.iconUri.isEmpty()) {
            Glide.with(context)
                    .load(UxUtils.getAppIcon(context, item))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(itemView.icon)
        } else {
            Glide.with(context)
                    .load(item.iconUri)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(itemView.icon)
        }

        itemView.badge.apply {
            if (GlobalValues.shortcutsList.contains(item.id)) {
                isVisible = true
                setImageResource(R.drawable.ic_add_shortcut)
                setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
            } else {
                isGone = true
            }
        }
        itemView.indicator.apply {
            if (item.type == AnywhereType.Card.SWITCH_SHELL) {
                isVisible = true
                if (item.param3 == SWITCH_OFF) {
                    setImageResource(R.drawable.ic_red_dot)
                } else if (item.param3 == SWITCH_ON) {
                    setImageResource(R.drawable.ic_green_dot)
                }
            } else {
                isGone = true
            }
        }

        if (!selectedIndex.contains(holder.layoutPosition)) {
            (holder.itemView as MaterialCardView).apply {
                scaleX = 1.0f
                scaleY = 1.0f
                isChecked = false
            }
        }
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        data.add(toPosition, data.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onSwiped(position: Int) { /* Do nothing */ }

    override fun getItemId(position: Int): Long = data[position].timeStamp.toLong()

    fun clickItem(v: View, position: Int) {
        try {
            val item = getItem(position)

            if (mode == ADAPTER_MODE_NORMAL) {
                if (isAppFrozen(context, item)) {
                    v.postDelayed({ notifyItemChanged(position) }, 500)
                }
                Opener.with(context)
                        .load(item)
                        .setOpenedListener(object :Opener.OnOpenListener{
                            override fun onOpened() {
                                if (GlobalValues.closeAfterLaunch) {
                                    (context as BaseActivity).finish()
                                }
                            }

                        })
                        .open()
            } else if (mode == ADAPTER_MODE_SELECT) {
                if (selectedIndex.isNotEmpty() && selectedIndex.contains(position)) {
                    (v as MaterialCardView).apply {
                        scaleX = 1.0f
                        scaleY = 1.0f
                        isChecked = false
                    }
                    selectedIndex.remove(position)
                } else {
                    (v as MaterialCardView).apply {
                        scaleX = 0.9f
                        scaleY = 0.9f
                        isChecked = true
                    }
                    selectedIndex.add(position)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun longClickItem(v: View, position: Int, isEditMode: Boolean = true): Boolean {
        try {
            val item = getItem(position)

            if (mode == ADAPTER_MODE_NORMAL) {

                val intent = Intent(context, EditorActivity::class.java).apply {
                    putExtra(EXTRA_ENTITY, item)
                    putExtra(EXTRA_EDIT_MODE, isEditMode)
                    putExtra(EXTRA_FROM_WORKFLOW, item.type == AnywhereType.Card.WORKFLOW)
                }

                if (GlobalValues.editorEntryAnim) {
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                            context as BaseActivity,
                            v,
                            context.getString(R.string.trans_item_container)
                    )

                    context.startActivity(intent, options.toBundle())
                } else {
                    context.startActivity(intent)
                }
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }

            return true
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            return false
        }
    }

    fun deleteSelect() {
        if (selectedIndex.size == 0) {
            return
        }
        val deleteList = mutableListOf<AnywhereEntity>()

        for (index in selectedIndex) {

            if (index < data.size) {
                deleteList.add(data[index])
            }
        }

        AnywhereApplication.sRepository.delete(deleteList)
        clearSelect()
    }

    fun moveSelect(pageTitle: String) {
        if (selectedIndex.size == 0) {
            return
        }
        val moveList = mutableListOf<AnywhereEntity>()

        for (index in selectedIndex) {

            if (index < data.size) {
                moveList.add(data[index])
            }
        }

        for (item in moveList) {
            item.category = pageTitle
            AnywhereApplication.sRepository.update(item)
        }
        clearSelect()
    }

    fun clearSelect() {
        selectedIndex.clear()
    }

    fun updateSortedList() {
        val list = mutableListOf<AnywhereEntity>()
        val startTime = System.currentTimeMillis()
        for (pos in 0 until data.size) {
            val item = data[pos]
            item.timeStamp = (startTime - pos * 100).toString()
            list.add(item)
        }
        AnywhereApplication.sRepository.update(list)
    }

    private fun shouldUpdateColorInfo(context: Context, item: AnywhereEntity): Boolean {
        return context !is QRCodeCollectionActivity && (item.type == AnywhereType.Card.ACTIVITY || item.type == AnywhereType.Card.URL_SCHEME || item.type == AnywhereType.Card.QR_CODE)
    }
}