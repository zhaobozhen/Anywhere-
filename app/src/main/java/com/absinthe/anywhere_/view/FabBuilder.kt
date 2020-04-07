package com.absinthe.anywhere_.view

import android.content.Context
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.GlobalValues
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

object FabBuilder {

    fun build(context: Context, fab: SpeedDialView) {
        val resources = context.resources
        fab.addActionItem(SpeedDialActionItem.Builder(R.id.fab_url_scheme, R.drawable.ic_url_scheme)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_url_scheme))
                .setLabelClickable(false)
                .create())
        fab.addActionItem(SpeedDialActionItem.Builder(R.id.fab_collector, R.drawable.ic_logo)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(GlobalValues.collectorMode)
                .setLabelClickable(false)
                .create())
        fab.addActionItem(SpeedDialActionItem.Builder(R.id.fab_activity_list, R.drawable.ic_activity_list)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_activity_list))
                .setLabelClickable(false)
                .create())
        fab.addActionItem(SpeedDialActionItem.Builder(R.id.fab_qr_code_collection, R.drawable.ic_qr_code)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_qr_code_collection))
                .setLabelClickable(false)
                .create())
        fab.addActionItem(SpeedDialActionItem.Builder(R.id.fab_advanced, R.drawable.ic_advanced_card)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_add_advanced_card))
                .setLabelClickable(false)
                .create())
    }
}