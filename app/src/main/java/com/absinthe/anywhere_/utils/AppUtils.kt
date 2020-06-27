package com.absinthe.anywhere_.utils

import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.FileUriExposedException
import android.os.Parcelable
import android.os.Process
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.*
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.manager.QRCollection
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.receiver.HomeWidgetProvider
import com.absinthe.anywhere_.ui.dialog.DynamicParamsDialogFragment
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_OFF
import com.absinthe.anywhere_.ui.editor.impl.SWITCH_ON
import com.absinthe.anywhere_.ui.qrcode.QRCodeCollectionActivity
import com.absinthe.anywhere_.ui.settings.LogcatActivity
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.LogRecorder
import com.absinthe.anywhere_.utils.manager.URLManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.catchingnow.icebox.sdk_client.IceBox
import com.google.gson.Gson
import java.io.File
import java.util.*

object AppUtils {
    /**
     * react the url scheme
     *
     * @param context to launch an intent
     * @param param1  param1
     * @param param2  param2
     * @param param3  param3
     */
    fun openUrl(context: Context, param1: String, param2: String, param3: String) {
        val url = (URLManager.ANYWHERE_SCHEME + URLManager.URL_HOST + "?"
                + "param1=" + param1
                + "&param2=" + param2
                + "&param3=" + param3
                + "&type=" + AnywhereType.Card.ACTIVITY)
        URLSchemeHandler.parse(url, context)
    }

    fun openNewURLScheme(context: Context) {
        val url = URLManager.ANYWHERE_SCHEME + URLManager.URL_HOST + "?param1=&type=${AnywhereType.Card.URL_SCHEME}"
        URLSchemeHandler.parse(url, context)
    }

    /**
     * Update Anywhere- widget
     *
     * @param context context
     */
    fun updateWidget(context: Context) {
        val intent = Intent(context, HomeWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context,
                            HomeWidgetProvider::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }

        context.sendBroadcast(intent)
    }

    /**
     * Judge that whether an app is frost
     *
     * @param context context
     * @param item    Anywhere- entity
     * @return true if the app is frost
     */
    @JvmStatic
    fun isAppFrozen(context: Context, item: AnywhereEntity): Boolean {
        val type = item.type
        val apkTempPackageName: String

        if (type == AnywhereType.Card.URL_SCHEME) {
            apkTempPackageName = if (android.text.TextUtils.isEmpty(item.param2)) {
                getPackageNameByScheme(context, item.param1)
            } else {
                item.param2
            }
            return try {
                IceBox.getAppEnabledSetting(context, apkTempPackageName) != 0 //0 means available
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        } else if (type == AnywhereType.Card.ACTIVITY) {
            return try {
                IceBox.getAppEnabledSetting(context, item.param1) != 0 //0 means available
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }
        return false
    }

    /**
     * Get apps list
     *
     * @param packageManager android package manager
     * @param showSystem     true if show system apps
     * @return apps list
     */
    fun getAppList(packageManager: PackageManager, showSystem: Boolean): List<AppListBean> {
        val list: MutableList<AppListBean> = ArrayList()
        try {
            val packageInfos = packageManager.getInstalledPackages(0)

            for (packageInfo in packageInfos) {
                //Filter system apps
                if (!showSystem) {
                    if (ApplicationInfo.FLAG_SYSTEM and packageInfo.applicationInfo.flags != 0) {
                        continue
                    }
                }
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue
                }

                val bean = AppListBean().apply {
                    this.packageName = packageInfo.packageName
                    this.appName = AppUtils.getAppName(packageInfo.packageName)
                    this.icon = if (GlobalValues.iconPack == Const.DEFAULT_ICON_PACK || GlobalValues.iconPack.isEmpty()) {
                        packageInfo.applicationInfo.loadIcon(packageManager)
                    } else {
                        com.absinthe.anywhere_.model.Settings.sIconPack.getDrawableIconForPackage(packageInfo.packageName, packageInfo.applicationInfo.loadIcon(packageManager))
                                ?: Utils.getApp().getDrawable(R.drawable.ic_logo)!!
                    }
                }
                list.add(bean)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list.sortBy { it.appName }

        return list
    }

    /**
     * Get device's Android ID
     *
     * @param context Context
     * @return Android ID
     */
    fun getAndroidId(context: Context): String {
        return Settings.System.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID
        )
    }

    /**
     * Take a persistable URI permission grant that has been offered. Once
     * taken, the permission grant will be remembered across device reboots.
     * Only URI permissions granted with
     * [Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION] can be persisted. If
     * the grant has already been persisted, taking it again will touch
     * [UriPermission.getPersistedTime].
     *
     */
    fun takePersistableUriPermission(context: Context, uri: Uri, intent: Intent) {
        val takeFlags = (intent.flags
                and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
        // Check for the freshest data.
        context.contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    /**
     * Restart App
     */
    fun restart() {
        Utils.getApp().packageManager.getLaunchIntentForPackage(Utils.getApp().packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            Utils.getApp().startActivity(it)
        }
    }

    /**
     *
     * Start recording log
     *
     * @param context Context
     */
    fun startLogcat(context: Context) {
        GlobalValues.sIsDebugMode = true
        val logRecorder = LogRecorder.Builder(context)
                .setLogFolderName(context.getString(R.string.logcat))
                .setLogFileNameSuffix(AppUtils.getAppName())
                .setLogFileSizeLimitation(256)
                .setLogLevel(LogRecorder.DEBUG)
                .setPID(Process.myPid())
                .build()
        LogRecorder.setInstance(logRecorder)
        NotifyUtils.createLogcatNotification(context)
        LogcatActivity.isStartCatching = true
    }

    /**
     *
     * Send selected log file to my mailbox
     *
     * @param context Context
     * @param file Log file
     */
    fun sendLogcat(context: Context, file: File?) {
        val emailIntent = Intent(Intent.ACTION_SEND)

        if (file != null) {
            emailIntent.apply {
                type = "application/octet-stream"

                val emailReceiver = arrayOf("zhaobozhen2025@gmail.com")
                val emailTitle = String.format("[%s] App Version Code: %s", context.getString(R.string.report_title), BuildConfig.VERSION_CODE)
                putExtra(Intent.EXTRA_EMAIL, emailReceiver)
                putExtra(Intent.EXTRA_SUBJECT, emailTitle)

                val emailContent = "${context.getString(R.string.report_describe)}\n" +
                        "App Version Name: ${BuildConfig.VERSION_NAME}\n" +
                        "App Version Code: ${BuildConfig.VERSION_CODE}\n" +
                        "Device: ${Build.MODEL}\n" +
                        "Android Version: ${Build.VERSION.RELEASE}"

                putExtra(Intent.EXTRA_TEXT, emailContent)

                val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                putExtra(Intent.EXTRA_STREAM, uri)
            }


            //Filter Email Apps
            val queryIntent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
            val resolveInfos = context.packageManager.queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY
                    or PackageManager.GET_RESOLVED_FILTER)
            val targetIntents = ArrayList<Intent>()

            for (info in resolveInfos) {
                val ai = info.activityInfo
                val intent = Intent(emailIntent).apply {
                    setPackage(ai.packageName)
                    component = ComponentName(ai.packageName, ai.name)
                }
                targetIntents.add(intent)
            }
            val chooser = Intent.createChooser(targetIntents.removeAt(0), context.getString(R.string.report_select_mail_app)).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(arrayOf<Parcelable>()))
            }
            context.startActivity(chooser)
        }
    }

    /**
     * Acquire su permission to app
     *
     * @param context context
     */
    fun acquireRootPerm(context: Context): Boolean {
        return SuProcess.acquireRootPerm(context)
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun atLeastR(): Boolean {
        return Build.VERSION.SDK_INT >= 30
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun atLeastP(): Boolean {
        return Build.VERSION.SDK_INT >= 28
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun atLeastO(): Boolean {
        return Build.VERSION.SDK_INT >= 26
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
    fun atLeastNMR1(): Boolean {
        return Build.VERSION.SDK_INT >= 25
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    fun atLeastN(): Boolean {
        return Build.VERSION.SDK_INT >= 24
    }

    /**
     * Get package name by url scheme
     *
     * @param context for get manager
     * @param url     for get package name
     */
    fun getPackageNameByScheme(context: Context, url: String): String {
        val resolveInfo = context.packageManager
                .queryIntentActivities(URLSchemeHandler.handleIntent(url), PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo.isNotEmpty()) {
            return resolveInfo[0].activityInfo.packageName
        }
        return ""
    }

    /**
     * Judge that whether an activity is exported
     *
     * @param context context
     * @param cn      componentName
     * @return true if the activity is exported
     */
    fun isActivityExported(context: Context, cn: ComponentName): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getActivityInfo(cn, 0).exported
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun openAnywhereEntity(context: Context, item: AnywhereEntity) {
        when (item.type) {
            AnywhereType.Card.QR_CODE -> {
                val qrId = if (context is QRCodeCollectionActivity) {
                    item.id
                } else {
                    item.param2
                }
                val entity = QRCollection.Singleton.INSTANCE.instance.getQREntity(qrId)
                entity?.launch()
            }
            AnywhereType.Card.IMAGE -> {
                DialogManager.showImageDialog((context as AppCompatActivity), item.param1)
            }
            AnywhereType.Card.ACTIVITY -> {
                when {
                    SuProcess.acquireRootPerm(context) -> {
                        Opener.with(context).load(item).open()
                    }
                    isActivityExported(context, ComponentName(item.param1, item.param2)) -> {
                        val extraBean: ExtraBean = Gson().fromJson(item.param3, ExtraBean::class.java)
                        val action = if (extraBean.action.isEmpty()) {
                            Intent.ACTION_VIEW
                        } else {
                            extraBean.action
                        }
                        val intent = Intent(action).apply {
                            component = ComponentName(item.param1, item.param2)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        if (extraBean.data.isNotEmpty()) {
                            intent.data = extraBean.data.toUri()
                        }

                        for (extra in extraBean.extras) {
                            when (extra.type) {
                                TYPE_STRING -> intent.putExtra(extra.key, extra.value)
                                TYPE_BOOLEAN -> intent.putExtra(extra.key, extra.value.toBoolean())
                                TYPE_INT -> intent.putExtra(extra.key, extra.value.toInt())
                                TYPE_LONG -> intent.putExtra(extra.key, extra.value.toLong())
                                TYPE_FLOAT -> intent.putExtra(extra.key, extra.value.toFloat())
                                TYPE_URI -> intent.putExtra(extra.key, extra.value.toUri())
                            }
                        }

                        context.startActivity(intent)
                    }
                    else -> {
                        ToastUtil.makeText(R.string.toast_change_work_mode)
                    }
                }
            }
            AnywhereType.Card.URL_SCHEME -> {
                if (item.param3.isNotEmpty()) {
                    DialogManager.showDynamicParamsDialog((context as AppCompatActivity), item.param3, object : DynamicParamsDialogFragment.OnParamsInputListener {
                        override fun onFinish(text: String?) {
                            try {
                                URLSchemeHandler.parse(item.param1 + text, context)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                if (e is ActivityNotFoundException) {
                                    ToastUtil.makeText(R.string.toast_no_react_url)
                                } else if (atLeastN()) {
                                    if (e is FileUriExposedException) {
                                        ToastUtil.makeText(R.string.toast_file_uri_exposed)
                                    }
                                }
                            }
                        }

                        override fun onCancel() {}
                    })
                } else {
                    try {
                        URLSchemeHandler.parse(item.param1, context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (e is ActivityNotFoundException) {
                            ToastUtil.makeText(R.string.toast_no_react_url)
                        } else if (atLeastN()) {
                            if (e is FileUriExposedException) {
                                ToastUtil.makeText(R.string.toast_file_uri_exposed)
                            }
                        }
                    }
                }
            }
            AnywhereType.Card.SHELL -> {
                val result = CommandUtils.execAdbCmd(item.param1)
                DialogManager.showShellResultDialog(context, result, null, null)
            }
            AnywhereType.Card.SWITCH_SHELL -> {
                Opener.with(context).load(item).open()
                val ae = AnywhereEntity(item).apply {
                    param3 = if (param3 == SWITCH_OFF) SWITCH_ON else SWITCH_OFF
                }
                AnywhereApplication.sRepository.update(ae)

                if (atLeastNMR1()) {
                    ShortcutsUtils.updateShortcut(ae)
                }
            }
            AnywhereType.Card.FILE -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = item.param1.toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.makeText(R.string.toast_no_react_url)
                }
            }
        }
    }
}