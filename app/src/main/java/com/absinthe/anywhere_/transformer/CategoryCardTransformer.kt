package com.absinthe.anywhere_.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.sin

class CategoryCardTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if (position < 0) {
            page.translationX = ((-position + sin(position * Math.PI / 2)) * page.width).toFloat()
        } else {
            page.translationX = ((position - sin(position * Math.PI / 2)) * page.width).toFloat()
        }
    }

}