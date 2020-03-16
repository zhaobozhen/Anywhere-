package com.absinthe.anywhere_.receiver

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.AnywhereType
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.services.AppRemoteViewsService
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity
import com.absinthe.anywhere_.utils.TextUtils

class HomeWidgetProvider : AppWidgetProvider() {
    /**
     * 每次窗口小部件被更新都调用一次该方法
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val thisWidget = ComponentName(context, HomeWidgetProvider::class.java)

        // 创建一个 RemoteView
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_home)

        // 把这个 Widget 绑定到 RemoteViewsService
        val intent = Intent(context, AppRemoteViewsService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0])

        // 设置适配器
        remoteViews.setRemoteAdapter(R.id.lv_list, intent)

        // 设置当显示的 widget_list 为空显示的 View
        remoteViews.setEmptyView(R.id.lv_list, R.layout.widget_home)

        // 点击列表触发事件
        val clickIntent = Intent(context, HomeWidgetProvider::class.java)
        // 设置 Action，方便在 onReceive 中区别点击事件
        clickIntent.action = CLICK_ACTION
        clickIntent.data = Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME))
        val pendingIntentTemplate = PendingIntent.getBroadcast(
                context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setPendingIntentTemplate(R.id.lv_list, pendingIntentTemplate)

        // 更新 Widget
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_list)
    }

    /**
     * 接收窗口小部件点击时发送的广播
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (CLICK_ACTION == intent.action) {
            val newIntent = Intent(context, ShortcutsActivity::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val ae: AnywhereEntity? = intent.getParcelableExtra(Const.INTENT_EXTRA_WIDGET_ENTITY)

            if (ae != null) {
                if (ae.anywhereType == AnywhereType.IMAGE) {
                    newIntent.action = ShortcutsActivity.ACTION_START_IMAGE
                    newIntent.putExtra(Const.INTENT_EXTRA_SHORTCUTS_CMD, ae.param1)
                } else {
                    newIntent.action = ShortcutsActivity.ACTION_START_FROM_WIDGET
                    val cmd = TextUtils.getItemCommand(ae)
                    newIntent.putExtra(Const.INTENT_EXTRA_WIDGET_COMMAND, cmd)
                }
                context.startActivity(newIntent)
            }
        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    /**
     * 当小部件大小改变时
     */
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    companion object {
        const val CLICK_ACTION = "com.absinthe.anywhere_.action.CLICK" // 点击事件的广播ACTION
    }
}