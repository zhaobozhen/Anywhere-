package com.absinthe.anywhere_.services.widget

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.provider.BaseColumns
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.provider.CoreProvider.Companion.URI_ANYWHERE_ENTITY
import com.absinthe.anywhere_.utils.AppUtils.updateWidget
import com.absinthe.anywhere_.utils.UxUtils.getAppIcon
import com.blankj.utilcode.util.ConvertUtils
import com.catchingnow.icebox.sdk_client.IceBox
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class AppRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Timber.d("intent = $intent")
        return RemoteViewsFactory(this.applicationContext)
    }

    inner class RemoteViewsFactory internal constructor(context: Context) :
        RemoteViewsService.RemoteViewsFactory {
        private val mContext: WeakReference<Context> = WeakReference(context)
        private val mList: MutableList<AnywhereEntity> = Collections.synchronizedList(mutableListOf<AnywhereEntity>())
        private val mWorkerHandler by lazy { Handler(mHandlerThread.looper) }
        private lateinit var mHandlerThread: HandlerThread

        /**
         * AppRemoteViewsFactory 调用时执行，这个方法执行时间超过 20 秒会报错
         * 如果耗时长的任务应该在 onDataSetChanged 或者 getViewAt 中处理
         */
        override fun onCreate() {
            Timber.d("onCreate")
            mHandlerThread = HandlerThread("RemoteViewsFactory")
            mHandlerThread.start()
            // 需要显示的数据
            mWorkerHandler.post {
                val cursor = mContext.get()!!.contentResolver.query(
                    URI_ANYWHERE_ENTITY,
                    null,
                    null,
                    null,
                    null
                )
                if (cursor == null) {
                    Timber.d("cursor == null")
                    return@post
                }

                mList.clear()
                val tempList = mutableListOf<AnywhereEntity>()
                while (cursor.moveToNext()) {
                    val info = AnywhereEntity().apply {
                        id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                        appName = cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME))
                        param1 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1))
                        param2 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2))
                        param3 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3))
                        type = cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE))
                    }
                    tempList.add(info)
                }
                cursor.close()
                when (GlobalValues.sortMode) {
                    Const.SORT_MODE_TIME_ASC -> tempList.sortBy { it.timeStamp }
                    Const.SORT_MODE_NAME_ASC -> tempList.sortBy { it.appName }
                    Const.SORT_MODE_NAME_DESC -> tempList.sortByDescending { it.appName }
                    Const.SORT_MODE_TIME_DESC -> tempList.sortByDescending { it.timeStamp }
                    else -> tempList.sortByDescending { it.id }
                }
                mList.addAll(tempList)
                mContext.get()?.let { updateWidget(it) }
            }
        }

        /**
         * 当调用 notifyAppWidgetViewDataChanged 方法时，触发这个方法
         * 例如：AppRemoteViewsFactory.notifyAppWidgetViewDataChanged();
         */
        override fun onDataSetChanged() {
            Timber.d("onDataSetChanged")

            // 需要显示的数据
            mWorkerHandler.post {
                val cursor = mContext.get()!!.contentResolver.query(
                    URI_ANYWHERE_ENTITY,
                    null, null, null, null
                )
                if (cursor == null) {
                    Timber.d("cursor == null")
                    return@post
                }
                mList.clear()
                val tempList = mutableListOf<AnywhereEntity>()
                while (cursor.moveToNext()) {
                    val info = AnywhereEntity().apply {
                        id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID))
                        appName = cursor.getString(cursor.getColumnIndex(AnywhereEntity.APP_NAME))
                        param1 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_1))
                        param2 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_2))
                        param3 = cursor.getString(cursor.getColumnIndex(AnywhereEntity.PARAM_3))
                        type = cursor.getInt(cursor.getColumnIndex(AnywhereEntity.TYPE))
                    }
                    tempList.add(info)
                }
                cursor.close()
                when (GlobalValues.sortMode) {
                    Const.SORT_MODE_TIME_ASC -> tempList.sortBy { it.timeStamp }
                    Const.SORT_MODE_NAME_ASC -> tempList.sortBy { it.appName }
                    Const.SORT_MODE_NAME_DESC -> tempList.sortByDescending { it.appName }
                    Const.SORT_MODE_TIME_DESC -> tempList.sortByDescending { it.timeStamp }
                    else -> tempList.sortByDescending { it.id }
                }
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
            mHandlerThread.quitSafely()
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
            if (mList.isEmpty() || position < 0 || position >= mList.size || mContext.get() == null) {
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
            val context = mContext.get() ?: return null
            val rv = RemoteViews(context.packageName, R.layout.item_widget_list)

            // 设置要显示的内容
            rv.setTextViewText(R.id.tv_title, content)
            val icon: Drawable? = if (ae.iconUri.isNullOrEmpty()) {
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