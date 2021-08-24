package com.absinthe.anywhere_.ui.list

import android.app.Dialog
import android.os.Bundle
import android.widget.ViewFlipper
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.manager.CardTypeIconGenerator
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.CardListDialogBuilder
import com.absinthe.libraries.utils.extensions.dp
import java.util.*

class CardListDialogFragment : AnywhereDialogFragment() {

  private lateinit var mBuilder: CardListDialogBuilder
  private var mListener: AppListAdapter.OnAppItemClickListener? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    mBuilder = CardListDialogBuilder(requireContext())
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
        listBeans.add(
          AppListBean(
            id = "-1", appName = getString(R.string.none), type = AnywhereType.Card.NOT_CARD,
            icon = CardTypeIconGenerator.getAdvancedIcon(
              requireContext(),
              AnywhereType.Card.NOT_CARD,
              45.dp
            )
          )
        )
        for (ae in it) {
          if (ae.type == AnywhereType.Card.URL_SCHEME
            || ae.type == AnywhereType.Card.IMAGE
            || ae.type == AnywhereType.Card.SHELL
          ) {
            listBeans.add(
              AppListBean(
                id = ae.id,
                appName = ae.appName,
                packageName = ae.param2.orEmpty(),
                className = ae.param1,
                icon = UxUtils.getAppIcon(requireContext(), ae.param2.orEmpty())
                  ?: CardTypeIconGenerator.getAdvancedIcon(requireContext(), ae.type, 45.dp),
                type = ae.type
              )
            )
          } else if (ae.type == AnywhereType.Card.ACCESSIBILITY || ae.type == AnywhereType.Card.WORKFLOW) {
            listBeans.add(
              AppListBean(
                id = ae.id,
                appName = ae.appName,
                packageName = ae.param2.orEmpty(),
                className = ae.description.orEmpty(),
                icon = CardTypeIconGenerator.getAdvancedIcon(requireContext(), ae.type, 45.dp),
                type = ae.type
              )
            )
          } else {
            listBeans.add(
              AppListBean(
                id = ae.id,
                appName = ae.appName,
                packageName = ae.param1,
                className = ae.param2.orEmpty(),
                icon = UxUtils.getAppIcon(requireContext(), ae.param1)
                  ?: CardTypeIconGenerator.getAdvancedIcon(requireContext(), ae.type, 45.dp),
                type = ae.type
              )
            )
          }
        }
        mBuilder.mAdapter.apply {
          setOnItemClickListener { _, _, position ->
            mListener?.onClick(getItem(position), position)
          }
          setList(listBeans)
        }
      }
    }
  }
}
