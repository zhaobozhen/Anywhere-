package com.absinthe.anywhere_.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.receiver.NotificationClickReceiver
import com.absinthe.anywhere_.utils.manager.LogRecorder
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.NotificationUtils.ChannelConfig

object NotifyUtils {

    const val COLLECTOR_CHANNEL_ID = "collector_channel"
    const val LOGCAT_CHANNEL_ID = "logcat_channel"
    const val COLLECTOR_NOTIFICATION_ID = 1001
    const val LOGCAT_NOTIFICATION_ID = 1002

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createCollectorChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = context.getText(R.string.notification_channel_collector)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(COLLECTOR_CHANNEL_ID, name, importance).apply {
            setShowBadge(false)
            setSound(null, null)
        }
        manager.createNotificationChannel(mChannel)
    }

    fun createLogcatNotification(context: Context) {
        val channelConfig = ChannelConfig(
                LOGCAT_CHANNEL_ID,
                context.getText(R.string.notification_channel_logcat),
                NotificationUtils.IMPORTANCE_DEFAULT)
        val intent = Intent(context, NotificationClickReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        NotificationUtils.notify(LOGCAT_NOTIFICATION_ID, channelConfig) { param: NotificationCompat.Builder ->
            param.setContentTitle(context.getString(R.string.notification_logcat_title))
                    .setContentText(context.getString(R.string.notification_logcat_content))
                    .setSmallIcon(R.drawable.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build()
            null
        }
        LogRecorder.getInstance().start()
    }
}