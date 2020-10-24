package com.absinthe.anywhere_.utils.manager

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.libraries.utils.extensions.dp

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

    fun getAdvancedIcon(context: Context, type: Int): Drawable {
        val iv = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(45.dp, 45.dp)
            setImageResource(getIconRes(type))
            setBackgroundResource(R.drawable.bg_circle)
            backgroundTintList = ColorStateList.valueOf(context.getColor(COLORS[type]))
        }
        return iv.drawable
    }

    private fun getIconRes(type: Int): Int {
        return when (type) {
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