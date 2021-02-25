package com.absinthe.anywhere_.services.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.model.database.AnywhereEntity;
import com.absinthe.anywhere_.provider.CoreProvider;
import com.absinthe.anywhere_.utils.UxUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.catchingnow.icebox.sdk_client.IceBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class AppRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final WeakReference<Context> mContext;
        private final List<AnywhereEntity> mList = new ArrayList<>();

        RemoteViewsFactory(Context context, Intent intent) {
            mContext = new WeakReference<>(context);
        }

        /**
         * AppRemoteViewsFactory 调用时执行，这个方法执行时间超过 20 秒会报错
         * 如果耗时长的任务应该在 onDataSetChanged 或者 getViewAt 中处理
         */
        @Override
        public void onCreate() {
            // 需要显示的数据
            new Thread(() -> {
                Cursor cursor = mContext.get().getContentResolver().query(CoreProvider.Companion.getURI_ANYWHERE_ENTITY(), null, null, null, null);
                if (cursor == null) {
                    return;
                }

                mList.clear();

                while (cursor.moveToNext()) {
                    AnywhereEntity info = AnywhereEntity.Builder();
                    info.setAppName(cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME)));
                    info.setParam1(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1)));
                    info.setParam2(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2)));
                    info.setParam3(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3)));
                    info.setType(cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE)));

                    mList.add(info);
                }

                cursor.close();
            }).start();
        }

        /**
         * 当调用 notifyAppWidgetViewDataChanged 方法时，触发这个方法
         * 例如：AppRemoteViewsFactory.notifyAppWidgetViewDataChanged();
         */
        @Override
        public void onDataSetChanged() {
            // 需要显示的数据
            new Thread(() -> {
                Cursor cursor = mContext.get().getContentResolver().query(CoreProvider.Companion.getURI_ANYWHERE_ENTITY(),
                        null, null, null, null);
                if (cursor == null) {
                    return;
                }

                mList.clear();
                while (cursor.moveToNext()) {
                    AnywhereEntity info = AnywhereEntity.Builder();
                    info.setId(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)));
                    info.setAppName(cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME)));
                    info.setParam1(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1)));
                    info.setParam2(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2)));
                    info.setParam3(cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3)));
                    info.setType(cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE)));

                    mList.add(info);
                }

                cursor.close();
            }).start();
        }

        /**
         * 这里写清理资源，释放内存的操作
         */
        @Override
        public void onDestroy() {
            mList.clear();
        }

        /**
         * 返回集合数量
         */
        @Override
        public int getCount() {
            return mList.size();
        }

        /**
         * 创建并且填充，在指定索引位置显示的 View
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= mList.size()) {
                return null;
            }
            String content = mList.get(position).getAppName();

            try {
                if (IceBox.getAppEnabledSetting(mContext.get(), mList.get(position).getParam1()) != 0) {
                    content = "\u2744" + content;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(e);
            }

            // 创建在当前索引位置要显示的View
            final RemoteViews rv = new RemoteViews(mContext.get().getPackageName(), R.layout.item_widget_list);

            // 设置要显示的内容
            rv.setTextViewText(R.id.tv_title, content);

            Drawable icon;
            AnywhereEntity ae = mList.get(position);

            if (ae.getIconUri().isEmpty()) {
                icon = UxUtils.INSTANCE.getAppIcon(AppRemoteViewsService.this, ae);
            } else {
                try {
                    icon = Drawable.createFromStream(getContentResolver().openInputStream(Uri.parse(ae.getIconUri())), null);
                } catch (Exception e) {
                    Timber.e(e);
                    icon = UxUtils.INSTANCE.getAppIcon(AppRemoteViewsService.this, ae);
                }
            }

            if (icon != null) {
                rv.setImageViewBitmap(R.id.iv_app_icon, ConvertUtils.drawable2Bitmap(icon));
            }

            // 填充Intent，填充在AppWidgetProvider中创建的PendingIntent
            Intent intent = new Intent();
            // 传入点击行的数据
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