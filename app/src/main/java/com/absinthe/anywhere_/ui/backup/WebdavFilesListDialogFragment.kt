package com.absinthe.anywhere_.ui.backup

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.applist.MODE_ICON_PACK
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.WebdavFilesListBuilder
import java.util.*

class WebdavFilesListDialogFragment :AnywhereDialogFragment() {

    private lateinit var mBuilder: WebdavFilesListBuilder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = WebdavFilesListBuilder(requireContext())
        initView()

        return AnywhereDialogBuilder(requireContext()).setView(mBuilder.root)
                .setTitle(R.string.dialog_title_choose_icon_pack)
                .create()
    }

    private fun initView() {
        val adapter = AppListAdapter(MODE_ICON_PACK)
        val hashMap = Settings.sIconPackManager.getAvailableIconPacks(true)
        val listBeans: MutableList<AppListBean> = ArrayList()

        for ((_, iconPack) in hashMap) {
            listBeans.add(AppListBean(iconPack.name, iconPack.packageName, "", -1))
        }
        adapter.apply {
            setOnItemClickListener { _, _, position ->

            }
            setList(listBeans)
        }

        mBuilder.rvIconPack.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

}