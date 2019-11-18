package com.absinthe.anywhere_.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseUtil {

    /**
     * Log event and upload to Firebase
     *
     * @param firebaseAnalytics Firebase entity
     * @param id Event id
     * @param name Event name
     */
    public static void logEvent(FirebaseAnalytics firebaseAnalytics, String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
