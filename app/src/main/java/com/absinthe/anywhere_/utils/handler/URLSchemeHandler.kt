package com.absinthe.anywhere_.utils.handler

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

object URLSchemeHandler {

    @Throws(ActivityNotFoundException::class)
    fun parse(url: String?, context: Context) {
        try {
            context.startActivity(handleIntent(url))
        } catch (e: ActivityNotFoundException) {
            throw ActivityNotFoundException()
        } catch (e: SecurityException) {
            throw ActivityNotFoundException()
        }
    }

    @Throws(ActivityNotFoundException::class)
    fun parse(url: String?, activity: Activity) {
        try {
            activity.startActivity(handleIntent(url))
        } catch (e: ActivityNotFoundException) {
            throw ActivityNotFoundException()
        } catch (e: SecurityException) {
            throw ActivityNotFoundException()
        }
    }

    @Throws(ActivityNotFoundException::class)
    fun parseForResult(url: String?, activity: Activity, requestCode: Int) {
        try {
            activity.startActivityForResult(handleIntent(url), requestCode)
        } catch (e: ActivityNotFoundException) {
            throw ActivityNotFoundException()
        } catch (e: SecurityException) {
            throw ActivityNotFoundException()
        }
    }

    @JvmStatic
    @Throws(ActivityNotFoundException::class)
    fun parse(url: String?, fragment: Fragment) {
        try {
            fragment.startActivity(handleIntent(url))
        } catch (e: ActivityNotFoundException) {
            throw ActivityNotFoundException()
        } catch (e: SecurityException) {
            throw ActivityNotFoundException()
        }
    }

    @Throws(ActivityNotFoundException::class)
    fun parseForResult(url: String?, fragment: Fragment, requestCode: Int) {
        try {
            fragment.startActivityForResult(handleIntent(url), requestCode)
        } catch (e: ActivityNotFoundException) {
            throw ActivityNotFoundException()
        } catch (e: SecurityException) {
            throw ActivityNotFoundException()
        }
    }

    @JvmStatic
    fun handleIntent(url: String?): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}