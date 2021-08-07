package com.absinthe.anywhere_.adapter.a11y.provider

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yType
import com.absinthe.anywhere_.adapter.a11y.TYPE_COORD
import com.absinthe.anywhere_.adapter.a11y.bean.A11yBaseBean
import com.absinthe.anywhere_.services.overlay.CollectorService
import com.absinthe.anywhere_.services.overlay.ICollectorListener
import com.absinthe.anywhere_.services.overlay.ICollectorService
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.textfield.TextInputEditText

class A11yCoordProvider : BaseItemProvider<A11yBaseBean>() {

    override val itemViewType: Int = TYPE_COORD
    override val layoutId: Int = R.layout.item_a11y_coord

    private var isBound = false
    private var currentPosition = -1
    private var textView: TextView? = null
    private var collectorService: ICollectorService? = null
    private val collectorListener = object : ICollectorListener.Stub() {
        @SuppressLint("SetTextI18n")
        override fun onCoordinatorSelected(x: Int, y: Int) {
            getAdapter()?.let {
                it.data[currentPosition].actionBean.content = "$x, $y"
            }
            textView?.text = "$x, $y"
        }
    }
    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            collectorService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            collectorService = ICollectorService.Stub.asInterface(service)
            collectorService?.registerCollectorListener(collectorListener)
            collectorService?.startCoordinator()
            getAdapter()?.let {
                if (PermissionUtils.isGrantedDrawOverlays()) {
                    ActivityUtils.startLauncherActivity(it.data[currentPosition].actionBean.pkgName)
                }
            }
        }

    }

    init {
        addChildClickViewIds(R.id.ib_remove)
        addChildClickViewIds(R.id.btn_select)
    }

    override fun convert(helper: BaseViewHolder, item: A11yBaseBean) {
        helper.getView<TextView>(R.id.tv_title).apply {
            text = when (item.actionBean.type) {
                A11yType.COORDINATE -> context.getString(R.string.bsd_a11y_menu_click_coord)
                A11yType.LONG_PRESS_COORDINATE -> context.getString(R.string.bsd_a11y_menu_long_press_coord)
                else -> throw IllegalArgumentException("wrong a11y type")
            }
        }
        helper.getView<TextInputEditText>(R.id.tiet_activity_id).apply {
            setText(item.actionBean.activityId)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.actionBean.activityId = s.toString()
                }
            })
        }
        helper.getView<TextInputEditText>(R.id.tiet_text).apply {
            setText(item.actionBean.content)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    item.actionBean.content = s.toString()
                }
            })
        }
        helper.getView<EditText>(R.id.et_delay_time).apply {
            if (item.actionBean.delay != 0L) {
                setText(item.actionBean.delay.toString())
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    try {
                        item.actionBean.delay = s.toString().toLong()
                    } catch (ignore: Exception) {
                    }
                }
            })
        }
    }

    override fun onChildClick(
        helper: BaseViewHolder,
        view: View,
        data: A11yBaseBean,
        position: Int
    ) {
        if (view.id == R.id.ib_remove) {
            getAdapter()?.remove(data)
        } else if (view.id == R.id.btn_select) {
            currentPosition = position
            textView = helper.getView<TextInputEditText>(R.id.tiet_text)

            if (isBound) {
                collectorService?.startCoordinator()
                if (PermissionUtils.isGrantedDrawOverlays()) {
                    ActivityUtils.startLauncherActivity(data.actionBean.pkgName)
                }
            } else {
                context.bindService(Intent(context, CollectorService::class.java), conn, Context.BIND_AUTO_CREATE)
            }
        }
    }
}