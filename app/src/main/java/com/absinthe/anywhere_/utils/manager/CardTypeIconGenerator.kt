package com.absinthe.anywhere_.utils.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType

object CardTypeIconGenerator {

    private val COLORS = listOf(
        R.color.material_blue_300,
        R.color.material_red_300,
        0,
        R.color.material_green_300,
        R.color.material_pink_300,
        R.color.material_deep_purple_300,
        R.color.material_cyan_300,
        R.color.material_lime_300,
        R.color.material_indigo_300,
        R.color.material_deep_orange_300,
        R.color.material_amber_300,
    )

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getAdvancedIcon(context: Context, type: Int, size: Int): Drawable {
        val foreDrawable = context.getDrawable(getIconRes(type))?.apply {
            setTintList(ColorStateList.valueOf(Color.parseColor("#66FFFFFF")))
        }
        val backDrawable = context.getDrawable(R.drawable.bg_circle)?.apply {
            val colorRes = if (type == AnywhereType.Card.NOT_CARD) {
                context.getColor(R.color.material_blue_grey_300)
            } else {
                context.getColor(COLORS[type])
            }
            setTintList(ColorStateList.valueOf(colorRes))
        }
        return LayerDrawable(listOf(backDrawable, foreDrawable).toTypedArray()).apply {
            val inset = size / 4
            setLayerInset(1, inset, inset, inset, inset)
            setBounds(0, 0, size, size)
        }
    }

    private fun getIconRes(type: Int): Int {
        return when (type) {
            AnywhereType.Card.NOT_CARD -> R.drawable.ic_card_no
            AnywhereType.Card.URL_SCHEME -> R.drawable.ic_url_scheme
            AnywhereType.Card.ACTIVITY -> R.drawable.ic_card_activity
            AnywhereType.Card.QR_CODE -> R.drawable.ic_qr_code
            AnywhereType.Card.IMAGE -> R.drawable.ic_card_image
            AnywhereType.Card.SHELL -> R.drawable.ic_card_shell
            AnywhereType.Card.SWITCH_SHELL -> R.drawable.ic_card_switch
            AnywhereType.Card.FILE -> R.drawable.ic_card_file
            AnywhereType.Card.BROADCAST -> R.drawable.ic_card_broadcast
            AnywhereType.Card.WORKFLOW -> R.drawable.ic_card_workflow
            AnywhereType.Card.ACCESSIBILITY -> R.drawable.ic_card_accessibility
            else -> 0
        }
    }
}