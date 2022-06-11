package com.absinthe.anywhere_.utils

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.UriPermission
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.absinthe.anywhere_.receiver.HomeWidgetProvider
import com.absinthe.anywhere_.ui.settings.LogcatActivity
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.LogRecorder
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.libraries.me.Absinthe
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.catchingnow.icebox.sdk_client.IceBox
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File

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
    val url = getUrlByParam(param1, param2, param3)
    URLSchemeHandler.parse(context, url)
  }

  fun getUrlByParam(param1: String, param2: String, param3: String, fromCollector: Boolean = false): String {
    return Uri.Builder().scheme(URLManager.ANYWHERE_SCHEME_RAW)
      .authority(URLManager.URL_HOST)
      .appendQueryParameter("param1", param1)
      .appendQueryParameter("param2", param2)
      .appendQueryParameter("param3", param3)
      .appendQueryParameter("type", AnywhereType.Card.ACTIVITY.toString())
      .appendQueryParameter("fromCollector", fromCollector.toString())
      .build()
      .toString()
  }

  fun openNewURLScheme(context: Context) {
    val url = Uri.Builder().scheme(URLManager.ANYWHERE_SCHEME_RAW)
      .authority(URLManager.URL_HOST)
      .appendQueryParameter("param1", "")
      .appendQueryParameter("type", AnywhereType.Card.URL_SCHEME.toString())
      .build()
      .toString()
    URLSchemeHandler.parse(context, url)
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
        ?.getAppWidgetIds(ComponentName(context, HomeWidgetProvider::class.java))
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
  fun isAppFrozen(context: Context, item: AnywhereEntity): Boolean {
    val type = item.type
    val apkTempPackageName: String

    if (type == AnywhereType.Card.URL_SCHEME) {
      apkTempPackageName = if (android.text.TextUtils.isEmpty(item.param2)) {
        getPackageNameByScheme(context, item.param1)
      } else {
        item.param2.orEmpty()
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

        val bean = AppListBean(
          id = packageInfo.packageName,
          packageName = packageInfo.packageName,
          appName = AppUtils.getAppName(packageInfo.packageName),
          icon = if (GlobalValues.iconPack == Const.DEFAULT_ICON_PACK || GlobalValues.iconPack.isEmpty()) {
            packageInfo.applicationInfo.loadIcon(packageManager)
          } else {
            com.absinthe.anywhere_.model.Settings.iconPack?.getDrawableIconForPackage(
              packageInfo.packageName,
              packageInfo.applicationInfo.loadIcon(packageManager)
            )
              ?: ContextCompat.getDrawable(Utils.getApp(), R.drawable.ic_logo)!!
          },
          type = -1
        )
        list.add(bean)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    list.sortBy { it.appName }

    return list
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
  @SuppressLint("WrongConstant")
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

        val emailReceiver = arrayOf("absinthe@absinthe.life")
        val emailTitle = String.format(
          "[%s] App Version Code: %s",
          context.getString(R.string.report_title),
          BuildConfig.VERSION_CODE
        )
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
      val resolveInfos = context.packageManager.queryIntentActivities(
        queryIntent, PackageManager.MATCH_DEFAULT_ONLY
          or PackageManager.GET_RESOLVED_FILTER
      )
      val targetIntents = ArrayList<Intent>()

      for (info in resolveInfos) {
        val ai = info.activityInfo
        val intent = Intent(emailIntent).apply {
          setPackage(ai.packageName)
          component = ComponentName(ai.packageName, ai.name)
        }
        targetIntents.add(intent)
      }
      val chooser = Intent.createChooser(
        targetIntents.removeAt(0),
        context.getString(R.string.report_select_mail_app)
      ).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(arrayOf<Parcelable>()))
      }
      context.startActivity(chooser)
    }
  }

  /**
   *
   * Send shared card entity information to my mailbox
   *
   * @param context Context
   * @param entity Card entity
   */
  fun sendEntityToMailBox(context: Context, entity: AnywhereEntity) {
    val emailIntent = Intent(Intent.ACTION_SEND)

    val clearEntity = AnywhereEntity.getClearedEntity(entity)
    val content = Gson().toJson(clearEntity, AnywhereEntity::class.java)
    val encrypted = CipherUtils.encrypt(content)
    encrypted?.replace("\n".toRegex(), "")

    emailIntent.apply {
      type = "text/plain"

      val emailReceiver = arrayOf(Absinthe.EMAIL)
      val emailTitle =
        "[${context.getString(R.string.cloud_rules_email_title)}]${clearEntity.appName}"
      putExtra(Intent.EXTRA_EMAIL, emailReceiver)
      putExtra(Intent.EXTRA_SUBJECT, emailTitle)

      val emailContent = "${context.getString(R.string.cloud_rules_email_header)}\n" +
        "------------------------------------------\n\n" +
        "Type: ${AnywhereType.Card.NEW_TITLE_MAP[clearEntity.type]}\n\n" +
        encrypted

      putExtra(Intent.EXTRA_TEXT, emailContent)
    }

    //Filter Email Apps
    val queryIntent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
    val resolveInfos = context.packageManager.queryIntentActivities(
      queryIntent,
      PackageManager.MATCH_DEFAULT_ONLY or PackageManager.GET_RESOLVED_FILTER
    )
    val targetIntents = ArrayList<Intent>()

    for (info in resolveInfos) {
      val ai = info.activityInfo
      val intent = Intent(emailIntent).apply {
        setPackage(ai.packageName)
        component = ComponentName(ai.packageName, ai.name)
      }
      targetIntents.add(intent)
    }

    if (targetIntents.isEmpty()) {
      ToastUtil.makeText(R.string.toast_no_mail_app)
    } else {
      val chooser = Intent.createChooser(
        targetIntents.removeAt(0),
        context.getString(R.string.report_select_mail_app)
      ).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(arrayOf<Parcelable>()))
      }
      context.startActivity(chooser)
    }
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
  fun atLeastS(): Boolean {
    return Build.VERSION.SDK_INT >= 31
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
    try {
      val resolveInfo = context.packageManager
        .queryIntentActivities(
          URLSchemeHandler.handleIntent(url),
          PackageManager.MATCH_DEFAULT_ONLY
        )
      if (resolveInfo.isNotEmpty()) {
        return resolveInfo[0].activityInfo.packageName
      }
    } catch (e: Throwable) {
      Timber.e(e)
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
      Timber.e(e)
      false
    }
  }

  fun setTransparentLauncherIcon(flag: Boolean) {
    val context = Utils.getApp()
    val packageManager = context.packageManager

    if (flag) {
      packageManager.setComponentEnabledSetting(
        ComponentName(context, context.packageName + ".MainAliasActivity"),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
      )
      packageManager.setComponentEnabledSetting(
        ComponentName(context, context.packageName + ".TransIconActivity"),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0
      )
    } else {
      packageManager.setComponentEnabledSetting(
        ComponentName(context, context.packageName + ".TransIconActivity"),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
      )
      packageManager.setComponentEnabledSetting(
        ComponentName(context, context.packageName + ".MainAliasActivity"),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0
      )
    }
  }

  fun isAnywhereEntityNeedRoot(ae: AnywhereEntity): Boolean {
    return when (ae.type) {
      AnywhereType.Card.ACTIVITY -> {
        val clsName = if (ae.param2.orEmpty().startsWith(".")) {
          ae.param1 + ae.param2
        } else {
          ae.param2
        }
        !isActivityExported(Utils.getApp(), ComponentName(ae.param1, clsName.orEmpty()))
        //Todo app not installed
      }
      AnywhereType.Card.SHELL -> {
        true
      }
      AnywhereType.Card.SWITCH_SHELL -> {
        true
      }
      AnywhereType.Card.WORKFLOW -> {
        val flowStepList: List<FlowStepBean>? = try {
          Gson().fromJson(ae.param1, object : TypeToken<List<FlowStepBean>>() {}.type)
        } catch (e: JsonSyntaxException) {
          null
        }

        flowStepList?.let { list ->
          list.forEach {
            it.entity?.let { entity ->
              return isAnywhereEntityNeedRoot(entity)
            }
          }
        }
        false
      }
      else -> false
    }
  }
}

/**
 * From drakeet
 */
fun doOnMainThreadIdle(action: () -> Unit, timeout: Long? = null) {
  val handler = Handler(Looper.getMainLooper())

  val idleHandler = MessageQueue.IdleHandler {
    handler.removeCallbacksAndMessages(null)
    action()
    return@IdleHandler false
  }

  fun setupIdleHandler(queue: MessageQueue) {
    if (timeout != null) {
      handler.postDelayed({
        queue.removeIdleHandler(idleHandler)
        action()
        if (BuildConfig.DEBUG) {
          Timber.d("${timeout}ms timeout!")
        }
      }, timeout)
    }
    queue.addIdleHandler(idleHandler)
  }

  if (Looper.getMainLooper() == Looper.myLooper()) {
    setupIdleHandler(Looper.myQueue())
  } else {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      setupIdleHandler(Looper.getMainLooper().queue)
    } else {
      handler.post { setupIdleHandler(Looper.myQueue()) }
    }
  }
}
