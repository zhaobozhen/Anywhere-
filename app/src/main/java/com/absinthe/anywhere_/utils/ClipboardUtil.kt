package com.absinthe.anywhere_.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle

object ClipboardUtil {

    @JvmStatic
    fun getClipBoardText(activity: Activity, f: Function) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getTextFroClipFromAndroidQ(activity, f)
        } else {
            f.invoke(getTextFromClip(activity))
        }
    }

    @JvmStatic
    fun clearClipboard(context: Context) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", "")
        cm.setPrimaryClip(mClipData)
    }

    /**
     * Android Q get content from clipboard
     */
    @TargetApi(Build.VERSION_CODES.Q)
    private fun getTextFroClipFromAndroidQ(activity: Activity, f: Function) {
        val runnable = Runnable label@{
            val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            try {
                if (!clipboardManager.hasPrimaryClip()) {
                    f.invoke("")
                    return@label
                }
                val clipData = clipboardManager.primaryClip
                if (null == clipData || clipData.itemCount < 1) {
                    f.invoke("")
                    return@label
                }
                val item = clipData.getItemAt(0)
                if (item == null) {
                    f.invoke("")
                    return@label
                }
                val clipText = item.text
                if (clipText.isNullOrEmpty()) f.invoke("") else f.invoke(clipText.toString())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        activity.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                activity.window.decorView.removeCallbacks(runnable)
            }
        })
        activity.window.decorView.post(runnable)
    }

    private fun getTextFromClip(context: Context): String {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboardManager.hasPrimaryClip()) {
            return ""
        }
        val clipData = clipboardManager.primaryClip
        if (null == clipData || clipData.itemCount < 1) {
            return ""
        }
        val item = clipData.getItemAt(0) ?: return ""
        val clipText = item.text
        return clipText.toString()
    }

    interface Function {
        /**
         * Invokes the function.
         */
        operator fun invoke(text: String)
    }
}