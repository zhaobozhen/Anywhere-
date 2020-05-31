package com.absinthe.anywhere_.adapter.card

import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.FileUriExposedException
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.ItemTouchCallBack
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.interfaces.OnPaletteFinishedListener
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.QRCollection
import com.absinthe.anywhere_.ui.fragment.DynamicParamsDialogFragment.OnParamsInputListener
import com.absinthe.anywhere_.ui.main.CategoryCardFragment
import com.absinthe.anywhere_.ui.main.EXTRA_COLOR
import com.absinthe.anywhere_.ui.main.EditorActivity
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.AppUtils.isAppFrozen
import com.absinthe.anywhere_.utils.CommandUtils.execAdbCmd
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler.parse
import com.absinthe.anywhere_.utils.manager.DialogManager.showDeleteAnywhereDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showDynamicParamsDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showImageDialog
import com.absinthe.anywhere_.utils.manager.DialogManager.showShellResultDialog
import com.absinthe.anywhere_.view.card.CardItemView
import com.absinthe.anywhere_.view.card.NormalItemView
import com.absinthe.anywhere_.view.card.StreamItemView
import com.absinthe.anywhere_.view.card.StreamSingleLineItemView
import com.absinthe.anywhere_.view.editor.*
import com.absinthe.anywhere_.view.editor.Editor.OnEditorListener
import com.bumptech.glide.Glide
import com.catchingnow.icebox.sdk_client.IceBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.card.MaterialCardView

const val ADAPTER_MODE_NORMAL = 0
const val ADAPTER_MODE_SORT = 1
const val ADAPTER_MODE_SELECT = 2

const val LAYOUT_MODE_NORMAL = 0
const val LAYOUT_MODE_STREAM = 1
const val LAYOUT_MODE_STREAM_SINGLE_LINE = 2

class BaseCardAdapter(val layoutMode: Int) : BaseQuickAdapter<AnywhereEntity, BaseViewHolder>(0), ItemTouchCallBack.OnItemTouchListener {

    var mode = ADAPTER_MODE_NORMAL
    private val mSelectedIndex = mutableListOf<Int>()
    private var mEditor: Editor<*>? = null

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
                normalView.content.param1.isGone = item.anywhereType == AnywhereType.QR_CODE
                normalView.content.param2.isGone = item.anywhereType == AnywhereType.URL_SCHEME
                        || item.anywhereType == AnywhereType.QR_CODE
                        || item.anywhereType == AnywhereType.IMAGE
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
                        UiUtils.setCardUseIconColor(itemView.cardBackground,
                                UiUtils.getAppIconByPackageName(context, item),
                                object : OnPaletteFinishedListener {
                                    override fun onFinished(color: Int) {
                                        if (color != 0) {
                                            itemView.appName.setTextColor(if (UiUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                            if (layoutMode == LAYOUT_MODE_STREAM) {
                                                normalView!!.content.description.setTextColor(if (UiUtils.isLightColor(color)) Color.BLACK else Color.WHITE)
                                            }
                                            item.color = color
                                            AnywhereApplication.sRepository.update(item)
                                        } else {
                                            itemView.appName.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                                            normalView?.content?.description?.setTextColor(ContextCompat.getColor(context, R.color.textColorNormal))
                                        }
                                    }
                                })
                    } else {
                        itemView.cardBackground.setBackgroundColor(item.color)
                        itemView.appName.setTextColor(if (UiUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                        normalView?.content?.description?.setTextColor(if (UiUtils.isLightColor(item.color)) Color.BLACK else Color.WHITE)
                    }
                } else if (GlobalValues.sCardBackgroundMode == Const.CARD_BG_MODE_GRADIENT) {
                    if (item.color == 0) {
                        UiUtils.setCardUseIconColor(itemView.cardBackground,
                                UiUtils.getAppIconByPackageName(context, item),
                                object : OnPaletteFinishedListener {
                                    override fun onFinished(color: Int) {
                                        item.color = color
                                        AnywhereApplication.sRepository.update(item)
                                    }
                                })
                    } else {
                        UiUtils.createLinearGradientBitmap(itemView.cardBackground, item.color, Color.TRANSPARENT)
                    }
                }
            }
        }

        if (item.iconUri.isEmpty()) {
            Glide.with(context)
                    .load(UiUtils.getAppIconByPackageName(context, item))
                    .into(itemView.icon)
        } else {
            Glide.with(context)
                    .load(item.iconUri)
                    .into(itemView.icon)
        }

        itemView.badge.apply {
            isGone = item.shortcutType != AnywhereType.SHORTCUTS && item.exportedType != AnywhereType.EXPORTED

            if (item.shortcutType == AnywhereType.SHORTCUTS) {
                setImageResource(R.drawable.ic_add_shortcut)
                setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
            } else if (item.exportedType == AnywhereType.EXPORTED) {
                setImageResource(R.drawable.ic_exported)
                setColorFilter(ContextCompat.getColor(context, R.color.exported_tint), PorterDuff.Mode.SRC_IN)
            }
        }

        if (!mSelectedIndex.contains(holder.layoutPosition)) {
            holder.itemView.scaleX = 1.0f
            holder.itemView.scaleY = 1.0f
            (holder.itemView as MaterialCardView).isChecked = false
        }
    }

    fun clickItem(v: View, position: Int) {
        try {
            val item = getItem(position)

            if (mode == ADAPTER_MODE_NORMAL) {
                if (isAppFrozen(context, item)) {
                    v.postDelayed({ notifyItemChanged(position) }, 500)
                }
                openAnywhereActivity(item)
            } else if (mode == ADAPTER_MODE_SELECT) {
                if (mSelectedIndex.contains(position)) {
                    v.scaleX = 1.0f
                    v.scaleY = 1.0f
                    (v as MaterialCardView).isChecked = false
                    mSelectedIndex.remove(position)
                } else {
                    v.scaleX = 0.9f
                    v.scaleY = 0.9f
                    (v as MaterialCardView).isChecked = true
                    mSelectedIndex.add(position)
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun longClickItem(v: View, position: Int): Boolean {
        try {
            val item = getItem(position)
            val type = item.anywhereType

            if (mode == ADAPTER_MODE_NORMAL) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

//                when (type) {
//                    AnywhereType.URL_SCHEME -> openEditor(item, Editor.URL_SCHEME, true)
//                    AnywhereType.ACTIVITY -> openEditor(item, Editor.ANYWHERE, true)
//                    AnywhereType.QR_CODE -> openEditor(item, Editor.QR_CODE, context !is QRCodeCollectionActivity)
//                    AnywhereType.IMAGE -> openEditor(item, Editor.IMAGE, true)
//                    AnywhereType.SHELL -> openEditor(item, Editor.SHELL, true)
//                    AnywhereType.SWITCH_SHELL -> openEditor(item, Editor.SWITCH_SHELL, true)
//                }
                val options = ActivityOptions.makeSceneTransitionAnimation(
                        context as BaseActivity,
                        v,
                        context.getString(R.string.trans_item_container)
                )
                context.startActivity(Intent(context, EditorActivity::class.java).apply {
                    putExtra(EXTRA_COLOR, if (item.color != 0) item.color else ContextCompat.getColor(context, R.color.colorPrimary))
                }, options.toBundle())
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

    private fun openAnywhereActivity(item: AnywhereEntity) {
        if (item.anywhereType == AnywhereType.QR_CODE) {
            val qrId = if (context is QRCodeCollectionActivity) {
                item.id
            } else {
                item.param2
            }
            val entity = QRCollection.Singleton.INSTANCE.instance.getQREntity(qrId)
            entity?.launch()
        } else if (item.anywhereType == AnywhereType.IMAGE) {
            showImageDialog((context as AppCompatActivity), item.param1)
        } else if (item.anywhereType == AnywhereType.URL_SCHEME) {
            if (item.param3.isNotEmpty()) {
                showDynamicParamsDialog((context as AppCompatActivity), item.param3, object : OnParamsInputListener {
                    override fun onFinish(text: String?) {
                        if (workingMode == Const.WORKING_MODE_URL_SCHEME) {
                            try {
                                parse(item.param1 + text, context as AppCompatActivity)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                if (e is ActivityNotFoundException) {
                                    ToastUtil.makeText(R.string.toast_no_react_url)
                                } else if (AppUtils.atLeastN()) {
                                    if (e is FileUriExposedException) {
                                        ToastUtil.makeText(R.string.toast_file_uri_exposed)
                                    }
                                }
                            }
                        } else {
                            Opener.with(context)
                                    .load(String.format(Const.CMD_OPEN_URL_SCHEME_FORMAT, item.param1) + text)
                                    .open()
                        }
                    }

                    override fun onCancel() {}
                })
            } else {
                Opener.with(context).load(item).open()
            }
        } else if (item.anywhereType == AnywhereType.SHELL) {
            val result = execAdbCmd(item.param1)
            showShellResultDialog(context, result, null, null)
        } else if (item.anywhereType == AnywhereType.SWITCH_SHELL) {
            Opener.with(context).load(item).open()
            if (item.param3 == SwitchShellEditor.SWITCH_SHELL_OFF_STATUS) {
                item.param3 = SwitchShellEditor.SWITCH_SHELL_ON_STATUS
                AnywhereApplication.sRepository.update(item)
            } else {
                item.param3 = SwitchShellEditor.SWITCH_SHELL_OFF_STATUS
                AnywhereApplication.sRepository.update(item)
            }
        } else if (item.anywhereType == AnywhereType.ACTIVITY) {
            Opener.with(context).load(item).open()
        }
    }

    private fun openEditor(item: AnywhereEntity, type: Int, isEditMode: Boolean) {
        val listener = OnEditorListener { deleteAnywhereActivity(item) }

        when (type) {
            Editor.ANYWHERE -> mEditor = AnywhereEditor(context)
            Editor.URL_SCHEME -> mEditor = SchemeEditor(context)
            Editor.QR_CODE -> mEditor = QRCodeEditor(context)
            Editor.IMAGE -> mEditor = ImageEditor(context)
            Editor.SHELL -> mEditor = ShellEditor(context)
            Editor.SWITCH_SHELL -> mEditor = SwitchShellEditor(context)
        }

        mEditor?.let {
            it.item(item)
                    .isEditorMode(isEditMode)
                    .isShortcut(item.shortcutType == AnywhereType.SHORTCUTS)
                    .isExported(item.exportedType == AnywhereType.EXPORTED)
                    .setOnEditorListener(listener)
                    .build()
            it.show()
        }
    }

    private fun deleteAnywhereActivity(ae: AnywhereEntity) {
        showDeleteAnywhereDialog(context, ae)
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        data.add(toPosition, data.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onSwiped(position: Int) {
    }
}