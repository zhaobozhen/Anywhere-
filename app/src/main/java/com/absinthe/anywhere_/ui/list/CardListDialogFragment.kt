package com.absinthe.anywhere_.ui.list

import android.app.Dialog
import android.os.Bundle
import android.widget.ViewFlipper
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CardListDialogBuilder
import java.util.*

class CardListDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: CardListDialogBuilder
    private var mListener: AppListAdapter.OnAppItemClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = CardListDialogBuilder(requireContext()).apply {
            setOnItemClickListener(mListener)
        }

        val builder = AnywhereDialogBuilder(requireContext())
        initView()

        return builder.setView(mBuilder.root).create()
    }

    fun setOnItemClickListener(listener: AppListAdapter.OnAppItemClickListener?) {
        mListener = listener
    }

    private fun setDisplayPlaceholder(flag: Boolean) {
        (mBuilder.root as ViewFlipper).displayedChild = if (flag) {
            CardListDialogBuilder.VF_EMPTY
        } else {
            CardListDialogBuilder.VF_LIST
        }
    }

    private fun initView() {
        val listBeans: MutableList<AppListBean> = ArrayList()
        AnywhereApplication.sRepository.allAnywhereEntities.value?.let {
            if (it.isEmpty()) {
                setDisplayPlaceholder(true)
            } else {
                setDisplayPlaceholder(false)
                for (ae in it) {
                    if (ae.anywhereType == AnywhereType.URL_SCHEME
                            || ae.anywhereType == AnywhereType.IMAGE
                            || ae.anywhereType == AnywhereType.SHELL) {
                        listBeans.add(AppListBean(ae.appName, ae.param2, ae.param1,
                                ae.anywhereType, UiUtils.getAppIconByPackageName(context, ae.param2)))
                    } else {
                        listBeans.add(AppListBean(ae.appName, ae.param1, ae.param2,
                                ae.anywhereType, UiUtils.getAppIconByPackageName(context, ae.param1)))
                    }
                }
                mBuilder.mAdapter.apply {
                    setOnItemClickListener { _, _, position ->
                        mListener?.onClick(getItem(position), position)
                    }
                    setNewInstance(listBeans)
                }
            }
        }
    }
}