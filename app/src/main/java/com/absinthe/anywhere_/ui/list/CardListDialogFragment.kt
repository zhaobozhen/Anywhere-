package com.absinthe.anywhere_.ui.list

import android.app.Dialog
import android.os.Bundle
import android.widget.ViewFlipper
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.AppListBean
import com.absinthe.anywhere_.utils.UiUtils
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CardListDialogBuilder
import java.util.*

class CardListDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: CardListDialogBuilder
    private var mListener: AppListAdapter.OnItemClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = CardListDialogBuilder(requireContext())
        val builder = AnywhereDialogBuilder(requireContext())

        mBuilder.setOnItemClickListener(mListener)
        initView()

        return builder.setView(mBuilder.root).create()
    }

    fun setOnItemClickListener(listener: AppListAdapter.OnItemClickListener?) {
        mListener = listener
    }

    private fun setDisplayPlaceholder(flag: Boolean) {
        if (flag) {
            (mBuilder.root as ViewFlipper).displayedChild = CardListDialogBuilder.VF_EMPTY
        } else {
            (mBuilder.root as ViewFlipper).displayedChild = CardListDialogBuilder.VF_LIST
        }
    }

    private fun initView() {
        val listBeans: MutableList<AppListBean> = ArrayList()
        val list = AnywhereApplication.sRepository.allAnywhereEntities?.value

        if (list != null) {
            if (list.isEmpty()) {
                setDisplayPlaceholder(true)
            } else {
                setDisplayPlaceholder(false)
                for (ae in list) {
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
                mBuilder.mAdapter.setList(listBeans)
            }
        }
    }
}