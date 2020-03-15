package com.absinthe.anywhere_.utils.timber

import android.util.Log
import com.absinthe.anywhere_.model.GlobalValues
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG) || GlobalValues.sIsDebugMode
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (!isLoggable(tag, priority)) {
            return
        }
        super.log(priority, tag, message, t)
    }
}