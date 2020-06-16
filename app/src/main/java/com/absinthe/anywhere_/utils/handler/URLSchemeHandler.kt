package com.absinthe.anywhere_.utils.handler

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

object URLSchemeHandler {

    @Throws(Exception::class)
    fun parse(url: String, context: Context) {
        context.startActivity(handleIntent(url))
    }

    @Throws(Exception::class)
    fun parse(url: String, activity: Activity) {
        activity.startActivity(handleIntent(url))
    }

    @Throws(Exception::class)
    fun parseForResult(url: String, activity: Activity, requestCode: Int) {
        activity.startActivityForResult(handleIntent(url), requestCode)
    }

    @Throws(Exception::class)
    fun parse(url: String, fragment: Fragment) {
        fragment.startActivity(handleIntent(url))
    }

    @Throws(Exception::class)
    fun parseForResult(url: String, fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(handleIntent(url), requestCode)
    }

    fun handleIntent(url: String): Intent {
        return Intent.parseUri(url, Intent.URI_INTENT_SCHEME or Intent.URI_ANDROID_APP_SCHEME)
    }
}