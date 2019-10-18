package com.absinthe.anywhere_.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.absinthe.anywhere_.provider.HomeWidgetProvider;

public class AppUtils {
    /**
     * react the url scheme
     * @param context to launch an intent
     * @param param1 param1
     * @param param2 param2
     * @param param3 param3
     */
    public static void openUrl(Context context, String param1, String param2, String param3) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String url = "anywhere://url?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3;
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, HomeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context,
                        HomeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
