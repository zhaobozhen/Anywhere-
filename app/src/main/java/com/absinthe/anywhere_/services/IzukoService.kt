package com.absinthe.anywhere_.services

import android.view.accessibility.AccessibilityEvent
import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.AppScope
import timber.log.Timber

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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Timber.e("onAccessibilityEvent $currentScope")
        super.onAccessibilityEvent(event)
    }

    override fun onPageUpdate(currentScope: AppScope) {
        super.onPageUpdate(currentScope)
        if (baseService == null) {
            baseService = this
        }
        if (gestureService == null) {
            gestureService = this
        }
        Timber.e("onPageUpdate $currentScope")
    }

}