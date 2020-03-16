package com.absinthe.anywhere_.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.absinthe.anywhere_.ui.settings.LogcatActivity
import com.absinthe.anywhere_.utils.manager.LogRecorder

class NotificationClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogRecorder.getInstance().stop()
        LogcatActivity.isStartCatching = false
        val newIntent = Intent(context, LogcatActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(newIntent)
    }
}