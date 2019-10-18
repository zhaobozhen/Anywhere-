package com.absinthe.anywhere_.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.SerializableAnywhereEntity;
import com.absinthe.anywhere_.services.AppRemoteViewsService;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.absinthe.anywhere_.utils.TextUtils;

public class HomeWidgetProvider extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.absinthe.anywhere_.action.CLICK"; // 点击事件的广播ACTION

    /**
     * 每次窗口小部件被更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName thisWidget = new ComponentName(context, HomeWidgetProvider.class);

        // 创建一个 RemoteView
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_home);

        // 把这个 Widget 绑定到 RemoteViewsService
        Intent intent = new Intent(context, AppRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);

        // 设置适配器
        remoteViews.setRemoteAdapter(R.id.lv_list, intent);

        // 设置当显示的 widget_list 为空显示的 View
        remoteViews.setEmptyView(R.id.lv_list, R.layout.widget_home);

        // 点击列表触发事件
        Intent clickIntent = new Intent(context, HomeWidgetProvider.class);
        // 设置 Action，方便在 onReceive 中区别点击事件
        clickIntent.setAction(CLICK_ACTION);
        clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(
                context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.lv_list, pendingIntentTemplate);

        // 更新 Widget
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_list);
    }

    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CLICK_ACTION.equals(intent.getAction())) {
            Intent newIntent = new Intent(context, ShortcutsActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setAction(ShortcutsActivity.ACTION_START_FROM_WIDGET);

            SerializableAnywhereEntity sae = (SerializableAnywhereEntity) intent.getSerializableExtra(Const.INTENT_EXTRA_WIDGET_ENTITY);
            if (sae != null) {
                String cmd = TextUtils.getItemCommand(sae);
                newIntent.putExtra(Const.INTENT_EXTRA_WIDGET_COMMAND, cmd);
                context.startActivity(newIntent);
            }

        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 当小部件大小改变时
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}