package com.absinthe.anywhere_.utils.timber;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber.DebugTree;

public class ThreadAwareDebugTree extends DebugTree {

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (tag != null) {
            String threadName = Thread.currentThread().getName();
            tag = "<" + threadName + "> " + tag;
        }
        super.log(priority, tag, message, t);
    }

    @Override
    protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
        return super.createStackElementTag(element) + " (Line " + element.getLineNumber() + ")";
    }
}
