package com.absinthe.anywhere_.ui.settings

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.tile.TileCardAdapter
import com.absinthe.anywhere_.databinding.ActivityTileSettingsBinding
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.AppListBean
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.services.TileOneService
import com.absinthe.anywhere_.services.TileThreeService
import com.absinthe.anywhere_.services.TileTwoService
import com.absinthe.anywhere_.utils.SPUtils.getString
import com.absinthe.anywhere_.utils.SPUtils.putString
import com.absinthe.anywhere_.utils.TextUtils
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.utils.manager.DialogManager.showCardListDialog
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.blankj.utilcode.util.ServiceUtils
import com.chad.library.adapter.base.BaseQuickAdapter

@RequiresApi(api = Build.VERSION_CODES.N)
open class TileSettingsActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTileSettingsBinding
    private var mAdapter: TileCardAdapter = TileCardAdapter()
    private var mList: List<AnywhereEntity> = ArrayList()

    override val isPaddingToolbar: Boolean
        get() = true

    override fun setViewBinding() {
        mBinding = ActivityTileSettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar
    }

    override fun initView() {
        super.initView()
        mBinding.rvTiles.layoutManager = LinearLayoutManager(this)
        mBinding.rvTiles.adapter = mAdapter

        mAdapter.setOnItemChildClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            if (view.id == R.id.btn_select) {
                val fragment = showCardListDialog(this)
                fragment.setOnItemClickListener(object : AppListAdapter.OnItemClickListener {
                    override fun onClick(bean: AppListBean?, which: Int) {
                        mAdapter.setData(position, bean)
                        var tile = ""
                        var tileLabel = ""
                        var tileCmd = ""
                        when (position) {
                            0 -> {
                                tile = Const.PREF_TILE_ONE
                                tileLabel = Const.PREF_TILE_ONE_LABEL
                                tileCmd = Const.PREF_TILE_ONE_CMD
                                if (!ServiceUtils.isServiceRunning(TileOneService::class.java)) {
                                    startService(Intent(this@TileSettingsActivity, TileOneService::class.java))
                                }
                            }
                            1 -> {
                                tile = Const.PREF_TILE_TWO
                                tileLabel = Const.PREF_TILE_TWO_LABEL
                                tileCmd = Const.PREF_TILE_TWO_CMD
                                if (!ServiceUtils.isServiceRunning(TileTwoService::class.java)) {
                                    startService(Intent(this@TileSettingsActivity, TileTwoService::class.java))
                                }
                            }
                            2 -> {
                                tile = Const.PREF_TILE_THREE
                                tileLabel = Const.PREF_TILE_THREE_LABEL
                                tileCmd = Const.PREF_TILE_THREE_CMD
                                if (!ServiceUtils.isServiceRunning(TileThreeService::class.java)) {
                                    startService(Intent(this@TileSettingsActivity, TileThreeService::class.java))
                                }
                            }
                            else -> {
                            }
                        }
                        putString(this@TileSettingsActivity, tile, mList[which].id)
                        putString(this@TileSettingsActivity, tileLabel, mList[which].appName)
                        putString(this@TileSettingsActivity, tileCmd, TextUtils.getItemCommand(mList[which]))
                        fragment.dismiss()
                    }
                })
            }
        }
        val viewModel = ViewModelProvider(this).get(AnywhereViewModel::class.java)
        viewModel.allAnywhereEntities?.observe(this, Observer { anywhereEntities: List<AnywhereEntity>? ->
            anywhereEntities?.let { mList = it }
            load()
        })
    }

    private fun initCard(): AppListBean {
        return AppListBean().apply {
            appName = getString(R.string.app_name)
            packageName = packageName
            className = localClassName
            icon = getDrawable(R.mipmap.ic_launcher)
        }
    }

    private fun initCard(item: AnywhereEntity): AppListBean {
        return AppListBean().apply {
            appName = item.appName
            packageName = item.param1
            className = item.param2
            icon = UiUtils.getAppIconByPackageName(this@TileSettingsActivity, item)
        }
    }

    private fun load() {
        if (getString(this, Const.PREF_TILE_ONE).isEmpty()) {
            mAdapter.addData(initCard())
        } else {
            val id = getString(this, Const.PREF_TILE_ONE)
            for (ae in mList) {
                if (ae.id == id) {
                    mAdapter.addData(initCard(ae))
                    break
                }
            }
        }
        if (getString(this, Const.PREF_TILE_TWO).isEmpty()) {
            mAdapter.addData(initCard())
        } else {
            val id = getString(this, Const.PREF_TILE_TWO)
            for (ae in mList) {
                if (ae.id == id) {
                    mAdapter.addData(initCard(ae))
                    break
                }
            }
        }
        if (getString(this, Const.PREF_TILE_THREE).isEmpty()) {
            mAdapter.addData(initCard())
        } else {
            val id = getString(this, Const.PREF_TILE_THREE)
            for (ae in mList) {
                if (ae.id == id) {
                    mAdapter.addData(initCard(ae))
                    break
                }
            }
        }
    }
}