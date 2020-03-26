package com.absinthe.anywhere_.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.model.AppListBean
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.IconPackDialogBuilder
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
        val adapter = AppListAdapter(context, AppListAdapter.MODE_ICON_PACK)
        val hashMap = Settings.sIconPackManager.getAvailableIconPacks(true)
        val listBeans: MutableList<AppListBean> = ArrayList()

        listBeans.add(AppListBean(context?.getString(R.string.bsd_default), Settings.DEFAULT_ICON_PACK, "", -1))
        for ((_, iconPack) in hashMap) {
            listBeans.add(AppListBean(iconPack.name, iconPack.packageName, "", -1))
        }
        adapter.setList(listBeans)

        mBuilder.rvIconPack.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }
}