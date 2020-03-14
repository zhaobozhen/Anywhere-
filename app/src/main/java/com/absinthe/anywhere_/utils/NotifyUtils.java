package com.absinthe.anywhere_.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.receiver.NotificationClickReceiver;
import com.absinthe.anywhere_.utils.manager.LogRecorder;
import com.blankj.utilcode.util.NotificationUtils;

public class NotifyUtils {

    public static final String COLLECTOR_CHANNEL_ID = "collector_channel";
    public static final String LOGCAT_CHANNEL_ID = "logcat_channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createCollectorChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = context.getText(R.string.notification_channel_collector);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(COLLECTOR_CHANNEL_ID, name, importance);
        mChannel.setShowBadge(false);
        mChannel.setSound(null, null);
        if (manager != null) {
            manager.createNotificationChannel(mChannel);
        }
    }

    public static void createLogcatNotification(Context context) {
        NotificationUtils.ChannelConfig channelConfig = new NotificationUtils.ChannelConfig(
                LOGCAT_CHANNEL_ID,
                context.getText(R.string.notification_channel_logcat),
                NotificationUtils.IMPORTANCE_DEFAULT);

        Intent intent = new Intent(context, NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        NotificationUtils.notify(1, channelConfig, param -> {
            param.setContentTitle(context.getString(R.string.notification_logcat_title))
                    .setContentText(context.getString(R.string.notification_logcat_content))
                    .setSmallIcon(R.drawable.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build();
            return null;
        });
        LogRecorder.getInstance().start();
    }
}
