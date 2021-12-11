package com.absinthe.anywhere_.view.home

import android.content.Context
import android.graphics.Color
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

object FabBuilder {

  fun build(context: Context, fab: SpeedDialView) {
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_advanced, R.drawable.ic_advanced_card)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(context.getString(R.string.btn_add_advanced_card))
        .setLabelClickable(false)
        .create()
    )
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_collector, R.drawable.ic_logo)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(GlobalValues.collectorMode)
        .setLabelClickable(false)
        .create()
    )
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_activity_list, R.drawable.ic_activity_list)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(context.getString(R.string.btn_activity_list))
        .setLabelClickable(false)
        .create()
    )
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_qr_code_collection, R.drawable.ic_qr_code)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(context.getString(R.string.btn_qr_code_collection))
        .setLabelClickable(false)
        .create()
    )
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_cloud_rules, R.drawable.ic_cloud_backup)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(context.getString(R.string.btn_cloud_rules))
        .setLabelClickable(false)
        .create()
    )
    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_third_apps_shortcut, R.drawable.ic_add_shortcut)
        .setFabBackgroundColor(Color.WHITE)
        .setFabImageTintColor(Color.BLACK)
        .setLabel(context.getString(R.string.btn_third_apps_shortcut))
        .setLabelClickable(false)
        .create()
    )
  }
}
