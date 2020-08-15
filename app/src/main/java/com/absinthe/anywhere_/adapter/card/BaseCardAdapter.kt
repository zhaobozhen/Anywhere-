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
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.listener.OnPaletteFinishedListener
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.ui.main.CategoryCardFragment
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val ADAPTER_MODE_NORMAL = 0
const val ADAPTER_MODE_SORT = 1
const val ADAPTER_MODE_SELECT = 2

const val LAYOUT_MODE_NORMAL = 0
const val LAYOUT_MODE_STREAM = 1
const val LAYOUT_MODE_STREAM_SINGLE_LINE = 2

class BaseCardAdapter(val layoutMode: Int) : BaseQuickAdapter<AnywhereEntity, BaseViewHolder>(0), ItemTouchCallBack.OnItemTouchListener {

    var mode = ADAPTER_MODE_NORMAL
    private val mSelectedIndex = mutableListOf<Int>()
    private val deleteItemSet = mutableSetOf<AnywhereEntity>()

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (layoutMode) {
            LAYOUT_MODE_NORMAL -> createBaseViewHolder(CardItemView(context, NormalItemView(context)))
            LAYOUT_MODE_STREAM -> createBaseViewHolder(CardItemView(context, StreamItemView(context)))
            LAYOUT_MODE_STREAM_SINGLE_LINE -> createBaseViewHolder(CardItemView(context, StreamSingleLineItemView(context)))
            else -> createBaseViewHolder(CardItemView(context, NormalItemView(context)))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun convert(holder: BaseViewHolder, item: AnywhereEntity) {

        val itemView = holder.itemView as CardItemView<*>

        val appName = try {
            if (IceBox.getAppEnabledSetting(context, item.packageName) != 0) {
                "\u2744" + item.appName
            } else {
                item.appName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            item.appName
        }
        itemView.appName.text = appName

        when (layoutMode) {
            LAYOUT_MODE_NORMAL -> {
                val normalView = itemView as CardItemView<NormalItemView>

                normalView.content.description.isGone = item.description.isEmpty()
                normalView.content.param1.isGone = item.type == AnywhereType.Card.QR_CODE
                normalView.content.param2.isGone = item.type == AnywhereType.Card.URL_SCHEME
                        || item.type == AnywhereType.Card.QR_CODE
                        || item.type == AnywhereType.Card.IMAGE
                normalView.content.description.text = item.description
                normalView.content.param1.text = item.param1
                normalView.content.param2.text = item.param2
            }
            LAYOUT_MODE_STREAM, LAYOUT_MODE_STREAM_SINGLE_LINE -> {
                val normalView: CardItemView<StreamItemView>? = if (layoutMode == LAYOUT_MODE_STREAM) {
                    itemView as CardItemView<StreamItemView>
                } else {
                    null
                }

                if (layoutMode == LAYOUT_MODE_STREAM) {
                    normalView!!.content.description.text = item.description
                }

                if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_PURE) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground,
                                UxUtils.getAppIcon(context, item),
                                object : OnPaletteFinishedListener {
                                    override fun onFinished(color: Int) {
                                        if (color != 0) {
                                            itemView.appName.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                            if (layoutMode == LAYOUT_MODE_STREAM) {
                                                normalView!!.content.description.setTextColor(if (UxUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                            }
                                            item.color = color

                                            if (shouldUpdateColorInfo(context, item)) {
                                                AnywhereApplication.sRepository.update(item)
                                            }
                                        } else {
                                            itemView.appName.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                                            normalView?.content?.description?.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                                        }
                                    }
                                })
                    } else {
                        itemView.rootView.backgroundTintList = ColorStateList.valueOf(item.color)
                        itemView.appName.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                        normalView?.content?.description?.setTextColor(if (UxUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                    }
                } else if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_GRADIENT) {
                    if (item.color == 0) {
                        UxUtils.setCardUseIconColor(itemView.cardBackground,
                                UxUtils.getAppIcon(context, item),
                                object : OnPaletteFinishedListener {
                                    override fun onFinished(color: Int) {
                                        item.color = color
                                        if (shouldUpdateColorInfo(context, item)) {
                                            AnywhereApplication.sRepository.update(item)
                                        }
                                    }
                                })
                        itemView.cardBackground.setImageDrawable(null)
                    } else {
                        (context as BaseActivity).lifecycleScope.launch(Dispatchers.IO) {
                            UxUtils.createLinearGradientBitmap(context as BaseActivity, itemView.cardBackground, item.color)
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
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemView.icon)
        } else {
            Glide.with(context)
                    .load(item.iconUri)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemView.icon)
        }

        if (GlobalValues.shortcutsList.contains(item.id)) {
            (itemView as ICard).addBadge()
            itemView.badge?.apply {
                setImageResource(R.drawable.ic_add_shortcut)
                setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
            }
        } else {
            (itemView as ICard).removeBadge()
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

        if (!mSelectedIndex.contains(holder.layoutPosition)) {
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

    override fun onSwiped(position: Int) {
    }

    override fun getItemId(position: Int): Long = try {
        data[position].id.toLong()
    } catch (e: NumberFormatException) {
        val item = data[position]
        val timestamp = System.currentTimeMillis()

        if (!deleteItemSet.contains(item)) {
            val ae = AnywhereEntity(item).apply {
                id = timestamp.toString()
                timeStamp = timestamp.toString()
            }
            AnywhereApplication.sRepository.delete(item)
            AnywhereApplication.sRepository.insert(ae)
        }

        deleteItemSet.add(item)
        timestamp
    }

    fun clickItem(v: View, position: Int) {
        try {
            val item = getItem(position)

            if (mode == ADAPTER_MODE_NORMAL) {
                if (isAppFrozen(context, item)) {
                    v.postDelayed({ notifyItemChanged(position) }, 500)
                }
                Opener.with(context).load(item).open()
            } else if (mode == ADAPTER_MODE_SELECT) {
                if (mSelectedIndex.contains(position)) {
                    (v as MaterialCardView).apply {
                        scaleX = 1.0f
                        scaleY = 1.0f
                        isChecked = false
                    }
                    mSelectedIndex.remove(position)
                } else {
                    (v as MaterialCardView).apply {
                        scaleX = 0.9f
                        scaleY = 0.9f
                        isChecked = true
                    }
                    mSelectedIndex.add(position)
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
        if (mSelectedIndex.size == 0) {
            return
        }
        val deleteList = mutableListOf<AnywhereEntity>()

        for (index in mSelectedIndex) {

            if (index < data.size) {
                deleteList.add(data[index])
            }
        }

        for (item in deleteList) {
            AnywhereApplication.sRepository.delete(item)
        }
        clearSelect()
    }

    fun clearSelect() {
        mSelectedIndex.clear()
    }

    fun updateSortedList() {
        CategoryCardFragment.refreshLock = true

        val startTime = System.currentTimeMillis()
        for (pos in 0 until data.size) {
            val item = data[pos]
            item.timeStamp = (startTime - pos * 100).toString()
            AnywhereApplication.sRepository.update(item)
        }

        CategoryCardFragment.refreshLock = false
    }

    private fun shouldUpdateColorInfo(context: Context, item: AnywhereEntity): Boolean {
        return context !is QRCodeCollectionActivity && (item.type == AnywhereType.Card.ACTIVITY || item.type == AnywhereType.Card.URL_SCHEME || item.type == AnywhereType.Card.QR_CODE)
    }
}