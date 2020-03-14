package com.absinthe.anywhere_.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.absinthe.anywhere_.ui.settings.LogcatActivity;
import com.absinthe.anywhere_.utils.manager.LogRecorder;

public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogRecorder.getInstance().stop();
        LogcatActivity.isStartCatching = false;
        Intent newIntent = new Intent(context, LogcatActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }
}
