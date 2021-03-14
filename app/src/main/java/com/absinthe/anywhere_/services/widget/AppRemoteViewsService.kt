package com.absinthe.anywhere_.services.widget

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.BaseColumns
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.provider.CoreProvider.Companion.URI_ANYWHERE_ENTITY
import com.absinthe.anywhere_.utils.AppUtils.updateWidget
import com.absinthe.anywhere_.utils.UxUtils.getAppIcon
import com.blankj.utilcode.util.ConvertUtils
import com.catchingnow.icebox.sdk_client.IceBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class AppRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return RemoteViewsFactory(this.applicationContext, intent)
    }

    inner class RemoteViewsFactory internal constructor(context: Context, intent: Intent?) :
        RemoteViewsService.RemoteViewsFactory {
        private val mContext: WeakReference<Context> = WeakReference(context)
        private val mList: MutableList<AnywhereEntity> = mutableListOf()

        /**
         * AppRemoteViewsFactory 调用时执行，这个方法执行时间超过 20 秒会报错
         * 如果耗时长的任务应该在 onDataSetChanged 或者 getViewAt 中处理
         */
        override fun onCreate() {
            Timber.d("onCreate")

            // 需要显示的数据
            GlobalScope.launch(Dispatchers.IO) {
                val cursor = mContext.get()!!.contentResolver.query(
                    URI_ANYWHERE_ENTITY,
                    null,
                    null,
                    null,
                    null
                )
                if (cursor == null) {
                    Timber.d("cursor == null")
                    return@launch
                }

                mList.clear()
                val tempList = mutableListOf<AnywhereEntity>()
                while (cursor.moveToNext()) {
                    val info = AnywhereEntity.Builder()
                    info.id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                    info.appName = cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME))
                    info.param1 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1))
                    info.param2 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2))
                    info.param3 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3))
                    info.type = cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE))
                    tempList.add(info)
                }
                cursor.close()
                tempList.sortByDescending { it.id }
                mList.addAll(tempList)
                updateWidget(mContext.get()!!)
            }
        }

        /**
         * 当调用 notifyAppWidgetViewDataChanged 方法时，触发这个方法
         * 例如：AppRemoteViewsFactory.notifyAppWidgetViewDataChanged();
         */
        override fun onDataSetChanged() {
            Timber.d("onDataSetChanged")

            // 需要显示的数据
            GlobalScope.launch(Dispatchers.IO) {
                val cursor = mContext.get()!!.contentResolver.query(
                    URI_ANYWHERE_ENTITY,
                    null, null, null, null
                )
                if (cursor == null) {
                    Timber.d("cursor == null")
                    return@launch
                }
                mList.clear()
                val tempList = mutableListOf<AnywhereEntity>()
                while (cursor.moveToNext()) {
                    val info = AnywhereEntity.Builder()
                    info.id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                    info.appName = cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME))
                    info.param1 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1))
                    info.param2 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2))
                    info.param3 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3))
                    info.type = cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE))
                    tempList.add(info)
                }
                cursor.close()
                tempList.sortByDescending { it.id }
                mList.addAll(tempList)
            }
        }

        /**
         * 这里写清理资源，释放内存的操作
         */
        override fun onDestroy() {
            Timber.d("onDestroy")
            mList.clear()
        }

        /**
         * 返回集合数量
         */
        override fun getCount(): Int {
            Timber.d("getCount == %s", mList.size)
            return mList.size
        }

        /**
         * 创建并且填充，在指定索引位置显示的 View
         */
        override fun getViewAt(position: Int): RemoteViews? {
            if (mList.isEmpty() || position < 0 || position >= mList.size) {
                return null
            }
            val ae = mList[position]
            var content = ae.appName
            try {
                if (IceBox.getAppEnabledSetting(mContext.get(), ae.param1) != 0) {
                    content = "\u2744" + content
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e)
            }

            // 创建在当前索引位置要显示的View
            val rv = RemoteViews(mContext.get()!!.packageName, R.layout.item_widget_list)

            // 设置要显示的内容
            rv.setTextViewText(R.id.tv_title, content)
            val icon: Drawable? = if (ae.iconUri.isEmpty()) {
                getAppIcon(this@AppRemoteViewsService, ae, ConvertUtils.dp2px(45f))
            } else {
                try {
                    Drawable.createFromStream(
                        contentResolver.openInputStream(Uri.parse(ae.iconUri)),
                        null
                    )
                } catch (e: Exception) {
                    Timber.e(e)
                    getAppIcon(this@AppRemoteViewsService, ae, ConvertUtils.dp2px(45f))
                }
            }
            if (icon != null) {
                rv.setImageViewBitmap(R.id.iv_app_icon, ConvertUtils.drawable2Bitmap(icon))
            }

            // 填充Intent，填充在AppWidgetProvider中创建的PendingIntent
            val intent = Intent()
            // 传入点击行的数据
            intent.putExtra(Const.INTENT_EXTRA_WIDGET_ENTITY, ae)
            rv.setOnClickFillInIntent(R.id.rl_item, intent)
            return rv
        }

        /**
         * 显示一个"加载" View。返回 null 的时候将使用默认的 View
         */
        override fun getLoadingView(): RemoteViews? {
            return null
        }

        /**
         * 不同 View 定义的数量。默认为1
         */
        override fun getViewTypeCount(): Int {
            return 1
        }

        /**
         * 返回当前索引的
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * 如果每个项提供的 ID 是稳定的，即它们不会在运行时改变，就返回 true
         */
        override fun hasStableIds(): Boolean {
            return true
        }
    }
}