package com.absinthe.anywhere_.utils.handler

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import java.net.URISyntaxException

object URLSchemeHandler {

    @Throws(Exception::class)
    fun parse(context: Context, url: String) {
        try {
            context.startActivity(handleIntent(url).apply {
                if (context !is Activity || url.startsWith("#Intent;")) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            })
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun parse(activity: Activity, url: String) {
        try {
            activity.startActivity(handleIntent(url))
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun parseForResult(activity: Activity, url: String, requestCode: Int) {
        try {
            activity.startActivityForResult(handleIntent(url), requestCode)
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun parse(fragment: Fragment, url: String) {
        try {
            fragment.startActivity(handleIntent(url).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun parseForResult(fragment: Fragment, url: String, requestCode: Int) {
        try {
            fragment.startActivityForResult(handleIntent(url).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, requestCode)
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(URISyntaxException::class)
    fun handleIntent(url: String): Intent {
        val intent: Intent
        try {
            intent = Intent.parseUri(url, 0)
        } catch (e: Throwable) {
            throw e
        }
        return intent
    }
}