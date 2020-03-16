package com.absinthe.anywhere_.workflow

import com.absinthe.anywhere_.services.IzukoService
import timber.log.Timber

class FlowNode(private val content: String, private val type: Int) {

    fun trigger() {
        Timber.d("trigger")
        when (type) {
            TYPE_ACCESSIBILITY_TEXT -> {
                IzukoService.isClicked(false)
                IzukoService.sInstance.clickTextViewByText(content)
                IzukoService.isClicked(true)
            }
            TYPE_ACCESSIBILITY_VIEW_ID -> {
                IzukoService.isClicked(false)
                IzukoService.sInstance.clickTextViewByID(content)
                IzukoService.isClicked(true)
            }
            else -> {
            }
        }
    }

    override fun toString(): String {
        return content
    }

    companion object {
        const val TYPE_ACCESSIBILITY_TEXT = 0
        const val TYPE_ACCESSIBILITY_VIEW_ID = 1
    }

}