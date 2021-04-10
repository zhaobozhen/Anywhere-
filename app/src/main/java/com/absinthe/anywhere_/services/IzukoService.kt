package com.absinthe.anywhere_.services

import cn.vove7.andro_accessibility_api.AccessibilityApi

class IzukoService : AccessibilityApi() {

    override val enableListenAppScope = true

    override fun onCreate() {
        baseService = this
        gestureService = this
        super.onCreate()
    }

    override fun onDestroy() {
        baseService = null
        gestureService = null
        super.onDestroy()
    }

}