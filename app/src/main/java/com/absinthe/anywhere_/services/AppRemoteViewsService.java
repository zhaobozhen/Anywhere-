package com.absinthe.anywhere_.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.ui.shortcuts.ShortcutsActivity;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.util.List;

public class AppRemoteViewsService extends RemoteViewsService {

    private static List<AnywhereEntity> sList;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;

        /**
         * 构造函数
         */
        RemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
            if (list != null && list.size() > 0) {
                sList = list;
            }

            if(Looper.myLooper() == null){
                Looper.prepare();
            }
        }

        /**
         * AppRemoteViewsFactory 调用时执行，这个方法执行时间超过 20 秒会报错
         * 如果耗时长的任务应该在 onDataSetChanged 或者 getViewAt 中处理
         */
        @Override
        public void onCreate() {
            // 需要显示的数据
            Intent intent = new Intent(AppRemoteViewsService.this, ShortcutsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        /**
         * 当调用 notifyAppWidgetViewDataChanged 方法时，触发这个方法
         * 例如：AppRemoteViewsFactory.notifyAppWidgetViewDataChanged();
         */
        @Override
        public void onDataSetChanged() {
            // 需要显示的数据
            List<AnywhereEntity> list = AnywhereApplication.sRepository.getAllAnywhereEntities().getValue();
            if (list != null && list.size() > 0) {
                sList = list;
            }
        }

        /**
         * 这里写清理资源，释放内存的操作
         */
        @Override
        public void onDestroy() {
            if (sList != null) {
                sList.clear();
                sList = null;
            }
        }

        /**
         * 返回集合数量
         */
        @Override
        public int getCount() {
            if (sList == null) {
                return 0;
            }
            return sList.size();
        }

        /**
         * 创建并且填充，在指定索引位置显示的 View
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (sList == null || position < 0 || position >= sList.size()) {
                return null;
            }
            String content = sList.get(position).getAppName();

            try {
                if (IceBox.getAppEnabledSetting(mContext, sList.get(position).getParam1()) != 0) {
                    content = content + "\u2744";
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Logger.e(e.getMessage());
            }

            // 创建在当前索引位置要显示的View
            final RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                    R.layout.item_widget_list);

            // 设置要显示的内容
            rv.setTextViewText(R.id.tv_title, content);
            rv.setImageViewBitmap(R.id.iv_app_icon,
                    UiUtils.drawableToBitmap(
                            UiUtils.getAppIconByPackageName(AppRemoteViewsService.this, sList.get(position))));

            // 填充Intent，填充在AppWidgetProvider中创建的PendingIntent
            Intent intent = new Intent();
            // 传入点击行的数据
            AnywhereEntity ae = sList.get(position);
            intent.putExtra(Const.INTENT_EXTRA_WIDGET_ENTITY, ae);
            rv.setOnClickFillInIntent(R.id.rl_item, intent);

            return rv;
        }

        /**
         * 显示一个"加载" View。返回 null 的时候将使用默认的 View
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * 不同 View 定义的数量。默认为1
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * 返回当前索引的
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 如果每个项提供的 ID 是稳定的，即它们不会在运行时改变，就返回 true
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}