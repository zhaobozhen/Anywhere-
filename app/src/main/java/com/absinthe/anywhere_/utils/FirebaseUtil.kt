package com.absinthe.anywhere_.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseUtil {
    /**
     * Log event and upload to Firebase
     *
     * @param firebaseAnalytics Firebase entity
     * @param id Event id
     * @param name Event name
     */
    @JvmStatic
    fun logEvent(firebaseAnalytics: FirebaseAnalytics, id: String?, name: String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, id)
            putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        })
    }
}