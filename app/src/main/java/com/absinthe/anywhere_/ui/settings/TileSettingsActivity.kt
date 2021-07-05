package com.absinthe.anywhere_.ui.settings

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.tile.TileCardAdapter
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityTileSettingsBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.services.tile.TILE_LABEL
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.manager.CardTypeIconGenerator
import com.absinthe.anywhere_.utils.manager.DialogManager.showCardListDialog
import com.absinthe.libraries.utils.extensions.dp
import com.chad.library.adapter.base.BaseQuickAdapter
import rikka.widget.borderview.BorderView


@RequiresApi(api = Build.VERSION_CODES.N)
open class TileSettingsActivity : AppBarActivity<ActivityTileSettingsBinding>() {

    private var mAdapter: TileCardAdapter = TileCardAdapter()
    private var mList: MutableList<AnywhereEntity> = mutableListOf()

    override fun setViewBinding() = ActivityTileSettingsBinding.inflate(layoutInflater)

    override fun getToolBar() = binding.toolbar.toolBar

    override fun getAppBarLayout() = binding.toolbar.appBar

    override fun initView() {
        super.initView()
        binding.apply {
            list.apply {
                layoutManager = LinearLayoutManager(this@TileSettingsActivity)
                adapter = mAdapter
                borderVisibilityChangedListener =
                    BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                        appBar?.setRaised(!top)
                    }
            }
        }

        mAdapter.setOnItemChildClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            if (view.id == R.id.btn_select) {
                showCardListDialog(this).apply {
                    setOnItemClickListener(object : AppListAdapter.OnAppItemClickListener {
                        override fun onClick(bean: AppListBean, which: Int) {
                            mAdapter.setData(position, bean)
                            val tile = "TileService${position + 1}"
                            val tileLabel = "TileService${position + 1}${TILE_LABEL}"
                            val appListBean = mList[which]

                            val state = if (appListBean.type == AnywhereType.Card.NOT_CARD) {
                                GlobalValues.mmkv.removeValueForKey(tile)
                                GlobalValues.mmkv.removeValueForKey(tileLabel)
                                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                            } else {
                                GlobalValues.mmkv.encode(tile, appListBean.id)
                                GlobalValues.mmkv.encode(tileLabel, appListBean.appName)
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            }
                            application.packageManager.setComponentEnabledSetting(
                                ComponentName(application.packageName, "${application.packageName}.services.tile.$tile"),
                                state, PackageManager.DONT_KILL_APP
                            )
                            dismiss()
                        }
                    })
                }
            }
        }
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            mList = it.toMutableList()
            mList.add(0, AnywhereEntity())
            load()
        }
    }

    private fun initCard(): AppListBean {
        return AppListBean(
            id = "-1",
            appName = getString(R.string.none),
            type = AnywhereType.Card.NOT_CARD,
            icon = CardTypeIconGenerator.getAdvancedIcon(this, AnywhereType.Card.NOT_CARD, 45.dp)
        )
    }

    private fun initCard(item: AnywhereEntity): AppListBean {
        return AppListBean(
                id = item.param1,
                appName = item.appName,
                packageName = item.param1,
                className = item.param2.orEmpty(),
                icon = UxUtils.getAppIcon(this@TileSettingsActivity, item, 45.dp),
                type = -1
        )
    }

    private fun load() {
        (1..7).forEach {
            loadImpl("TileService$it")
        }
    }

    private fun loadImpl(flag: String) {
        if (GlobalValues.mmkv.decodeString(flag).isNullOrEmpty()) {
            mAdapter.addData(initCard())
        } else {
            val id = GlobalValues.mmkv.decodeString(flag)
            mList.find { it.id == id }?.let {
                mAdapter.addData(initCard(it))
            } ?: let {
                mAdapter.addData(initCard())
            }
        }
    }
}