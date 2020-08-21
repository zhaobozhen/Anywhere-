package com.absinthe.anywhere_.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.applist.MODE_ICON_PACK
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.IconPackDialogBuilder
import com.blankj.utilcode.util.Utils
import java.util.*

class IconPackDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: IconPackDialogBuilder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = IconPackDialogBuilder(requireContext())
        initView()

        return AnywhereDialogBuilder(requireContext()).setView(mBuilder.root)
                .setTitle(R.string.dialog_title_choose_icon_pack)
                .create()
    }

    private fun initView() {
        val adapter = AppListAdapter(MODE_ICON_PACK)
        val hashMap = Settings.sIconPackManager.getAvailableIconPacks(true)
        val listBeans: MutableList<AppListBean> = ArrayList()

        listBeans.add(AppListBean(
                id = Const.DEFAULT_ICON_PACK,
                appName = requireContext().getString(R.string.bsd_default),
                packageName = Const.DEFAULT_ICON_PACK,
                type = -1
        ))
        for ((_, iconPack) in hashMap) {
            listBeans.add(AppListBean(
                    id = iconPack.packageName,
                    appName = iconPack.name,
                    packageName = iconPack.packageName,
                    type = -1
            ))
        }
        adapter.apply {
            setOnItemClickListener { _, _, position ->
                val item = getItem(position)
                GlobalValues.iconPack = item.packageName
                Settings.initIconPackManager(Utils.getApp())
                AppUtils.restart()
            }
            setList(listBeans)
        }

        mBuilder.rvIconPack.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }
}