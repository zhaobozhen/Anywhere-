package com.absinthe.anywhere_.utils.manager;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class TimeRecorder {
    private long startTime;
    private long endTime;
    private String event;

    public TimeRecorder(String event) {
        this.event = event;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public void log() {
        Logger.i("Event:", event, ", time consuming:", endTime - startTime, "ms");
    }

    public void logEvent(FirebaseAnalytics analytics) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
        analytics.logEvent("time_recorder", bundle);
    }
}
