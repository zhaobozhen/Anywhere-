package com.absinthe.anywhere_.utils.timber;

import android.util.Log;

import com.absinthe.anywhere_.model.GlobalValues;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class ReleaseTree extends Timber.DebugTree {

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
                || GlobalValues.sIsDebugMode;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        if (!isLoggable(tag, priority)) {
            return;
        }
        super.log(priority, tag, message, t);
    }

}
