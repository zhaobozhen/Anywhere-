package com.absinthe.anywhere_.workflow

import com.absinthe.anywhere_.services.IzukoService
import timber.log.Timber

class FlowNode(private val content: String, private val type: Int) {

    fun trigger() {
        Timber.d("trigger")

        when (type) {
            TYPE_ACCESSIBILITY_TEXT -> {
                IzukoService.getInstance()?.apply {
                    isClicked(false)
                    clickTextViewByText(content)
                    isClicked(true)
                }
            }
            TYPE_ACCESSIBILITY_VIEW_ID -> {
                IzukoService.getInstance()?.apply {
                    isClicked(false)
                    clickTextViewByID(content)
                    isClicked(true)
                }
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