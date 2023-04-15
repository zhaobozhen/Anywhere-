package com.absinthe.anywhere_.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.children
import androidx.palette.graphics.Palette
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues.actionBarType
import com.absinthe.anywhere_.constants.GlobalValues.autoDarkModeEnd
import com.absinthe.anywhere_.constants.GlobalValues.autoDarkModeStart
import com.absinthe.anywhere_.constants.GlobalValues.backgroundUri
import com.absinthe.anywhere_.constants.GlobalValues.iconPack
import com.absinthe.anywhere_.constants.GlobalValues.isPages
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.AppUtils.getPackageNameByScheme
import com.absinthe.anywhere_.utils.manager.CardTypeIconGenerator
import com.absinthe.anywhere_.view.home.TextSwitcherView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

object UxUtils {

  /**
   * Get app icon by package name
   *
   * @param context for get manager
   * @param item    for get package name
   */
  fun getAppIcon(context: Context, item: AnywhereEntity, size: Int): Drawable {
    val packageName: String
    return when (item.type) {
      AnywhereType.Card.URL_SCHEME -> {
        packageName = if (TextUtils.isEmpty(item.param2)) {
          getPackageNameByScheme(context, item.param1)
        } else {
          item.packageName
        }
        getAppIcon(context, packageName)
          ?: CardTypeIconGenerator.getAdvancedIcon(context, item.type, size)
      }
      AnywhereType.Card.ACTIVITY, AnywhereType.Card.QR_CODE -> getAppIcon(context, item.packageName)
        ?: CardTypeIconGenerator.getAdvancedIcon(context, item.type, size)
      else -> CardTypeIconGenerator.getAdvancedIcon(context, item.type, size)
    }
  }

  fun getAppIcon(context: Context, item: AppListBean, size: Int): Drawable {
    val ae = AnywhereEntity()
    if (item.type == AnywhereType.Card.URL_SCHEME || item.type == AnywhereType.Card.IMAGE || item.type == AnywhereType.Card.SHELL) {
      ae.param1 = item.className
      ae.param2 = item.packageName
    } else {
      ae.param1 = item.packageName
      ae.param2 = item.className
    }
    ae.type = item.type
    return getAppIcon(context, ae, size)
  }

  fun getAppIcon(context: Context, packageName: String): Drawable? {
    return try {
      if (iconPack == Const.DEFAULT_ICON_PACK || iconPack.isEmpty()) {
        context.packageManager.getApplicationIcon(packageName)
      } else {
        Settings.iconPack?.getDrawableIconForPackage(
          packageName,
          context.packageManager.getApplicationIcon(packageName)
        )
      }
    } catch (e: PackageManager.NameNotFoundException) {
      null
    }
  }

  fun getEntityIcon(context: Context, entity: AnywhereEntity, size: Int): Drawable =
    try {
      Drawable.createFromStream(
        context.contentResolver.openInputStream(Uri.parse(entity.iconUri)),
        null
      )
    } catch (e: Exception) {
      Timber.e(e)
      getAppIcon(context, entity, size)
    } ?: getAppIcon(context, entity, size)

  /**
   * get action bar title by working mode
   *
   * @return action bar title
   */
  fun getToolbarTitle(): String {
    val title = StringBuilder()

    when (workingMode) {
      Const.WORKING_MODE_URL_SCHEME -> title.append(AnywhereType.WhereMode.SOMEWHERE)
      Const.WORKING_MODE_ROOT, Const.WORKING_MODE_SHIZUKU -> title.append(AnywhereType.WhereMode.ANYWHERE)
      else -> title.append(AnywhereType.WhereMode.NOWHERE)
    }

    if (Settings.date == "12-25") {
      title.append(" \uD83C\uDF84")
    }
    return title.toString()
  }

  /**
   * Set action bar style
   *
   * @param activity Activity for bind action bar
   */
  fun setActionBarTransparent(activity: AppCompatActivity) {
    activity.supportActionBar?.setBackgroundDrawable(null)
  }

  /**
   * Judge that whether is light color
   *
   * @param color target color
   * @return true if is light color
   */
  fun isLightColor(color: Int): Boolean {
    //RGB 转化为 YUV 计算颜色灰阶判断深浅
    val grayScale =
      (Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114).toInt()
    return grayScale >= 192
  }

  /**
   * Judge that the action bar title color should be
   *
   * @param activity Activity for use Glide
   */
  fun setAdaptiveToolbarTitleColor(activity: BaseActivity<*>, textSwitcher: TextSwitcherView) {
    val title = getToolbarTitle()

    if (backgroundUri.isEmpty()) {
      return
    }

    if (actionBarType.isNotEmpty()) {
      setTopWidgetColor(activity, textSwitcher, actionBarType, title)
      return
    }

    Glide.with(activity.applicationContext)
      .asBitmap()
      .load(Uri.parse(backgroundUri))
      .into(object : CustomTarget<Bitmap?>() {
        override fun onLoadCleared(placeholder: Drawable?) {}
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
          val actionBarBitmap = Bitmap.createBitmap(
            resource, 0, 0,
            resource.width, BarUtils.getActionBarHeight().coerceAtMost(resource.height)
          )

          Palette.from(actionBarBitmap).generate { p: Palette? ->
            if (p != null) {
              //主导颜色,如果分析不出来，则返回默认颜色
              val dominantColor =
                p.getDominantColor(ContextCompat.getColor(activity, R.color.colorPrimary))
              if (isLightColor(dominantColor)) {
                // 深色字体
                setTopWidgetColor(activity, textSwitcher, Const.ACTION_BAR_TYPE_DARK, title)
              } else {
                // 浅色字体
                setTopWidgetColor(activity, textSwitcher, Const.ACTION_BAR_TYPE_LIGHT, title)
              }
            }
          }
        }
      })
  }

  /**
   * Set action bar title color and status bar and navigation bar style
   *
   * @param activity  Activity for bind action bar
   * @param textSwitcher our target
   * @param type      dark or light
   * @param title     action bar title
   */
  private fun setTopWidgetColor(
    activity: BaseActivity<*>,
    textSwitcher: TextSwitcherView,
    type: String,
    title: String
  ) {
    var newType = type

    if (backgroundUri.isEmpty()) {
      newType = if (activity.isNightMode()) {
        Const.ACTION_BAR_TYPE_LIGHT
      } else {
        Const.ACTION_BAR_TYPE_DARK
      }
    }

    val wic = WindowInsetsControllerCompat(activity.window, activity.window.decorView)

    if (newType == Const.ACTION_BAR_TYPE_DARK || newType.isEmpty()) {
      Timber.d("Dark-")
      val span = if (activity.isNightMode() && backgroundUri.isEmpty()) {
        textSwitcher.textColor = Color.WHITE
        ForegroundColorSpan(Color.WHITE)
      } else {
        textSwitcher.textColor = Color.BLACK
        ForegroundColorSpan(Color.BLACK)
      }
      textSwitcher.setText(SpannableString(title).apply {
        setSpan(span, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      })

      actionBarType = Const.ACTION_BAR_TYPE_DARK
      activity.invalidateOptionsMenu()

      if (backgroundUri.isNotEmpty() || isPages) {
        setActionBarTransparent(activity)
      }
      wic.isAppearanceLightStatusBars = true
    } else if (newType == Const.ACTION_BAR_TYPE_LIGHT) {
      Timber.d("Light-")
      val span = ForegroundColorSpan(Color.WHITE)
      textSwitcher.setText(SpannableString(title).apply {
        setSpan(span, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      })
      textSwitcher.textColor = Color.WHITE

      actionBarType = Const.ACTION_BAR_TYPE_LIGHT

      activity.invalidateOptionsMenu()
      wic.isAppearanceLightStatusBars = false
    }
  }

  /**
   * Make the card use icon's color
   *
   * @param drawable icon drawable
   * @param action notify when Palette finished
   */
  suspend fun setCardUseIconColor(drawable: Drawable, action: (color: Int) -> Unit) {
    coroutineScope {
      val bitmap = ConvertUtils.drawable2Bitmap(drawable) ?: return@coroutineScope
      val palette = Palette.from(bitmap).generate()

      withContext(Dispatchers.Main) {
        var color = palette.getVibrantColor(Color.TRANSPARENT)
        if (color == Color.TRANSPARENT) {
          color = palette.getDominantColor(Color.TRANSPARENT)
        }
        action(color)
      }
    }
  }

  /**
   * Judge that whether open the dark mode
   *
   * @return MODE_NIGHT_YES or MODE_NIGHT_NO
   */
  fun getAutoDarkMode(): Int {
    val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
    val minute = Calendar.getInstance()[Calendar.MINUTE]

    val start = Calendar.getInstance().apply {
      timeInMillis = autoDarkModeStart
    }
    val end = Calendar.getInstance().apply {
      timeInMillis = autoDarkModeEnd
    }

    var startHour = 22
    var startMinute = 0
    if (autoDarkModeStart != 0L) {
      startHour = start[Calendar.HOUR_OF_DAY]
      startMinute = start[Calendar.MINUTE]
    }

    var endHour = 7
    var endMinute = 0
    if (autoDarkModeEnd != 0L) {
      endHour = end[Calendar.HOUR_OF_DAY]
      endMinute = end[Calendar.MINUTE]
    }

    return if (startHour < endHour) {
      if (hour >= startHour && minute >= startMinute && hour <= endHour && minute <= endMinute) {
        AppCompatDelegate.MODE_NIGHT_YES
      } else {
        AppCompatDelegate.MODE_NIGHT_NO
      }
    } else {
      if (startHour == endHour && startMinute < endMinute) {
        if (hour == startHour && minute >= startMinute && minute <= endMinute) {
          AppCompatDelegate.MODE_NIGHT_YES
        } else {
          AppCompatDelegate.MODE_NIGHT_NO
        }
      } else {
        if (hour > startHour || hour == startHour && minute >= startMinute
          || hour < endHour || hour == endHour && minute <= endMinute
        ) {
          AppCompatDelegate.MODE_NIGHT_YES
        } else {
          AppCompatDelegate.MODE_NIGHT_NO
        }
      }
    }
  }

  /**
   * Create a linear gradient bitmap picture
   *
   * @param context context
   * @param view      target to set background
   * @param darkColor primary color
   */
  fun createLinearGradientBitmap(context: Context, view: ImageView, darkColor: Int) {
    val bgBitmap =
      Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas().apply {
      setBitmap(bgBitmap)
      drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }
    val lightColor = darkColor and 0x00ffffff or (128 shl 24)
    val gradient = LinearGradient(
      0f,
      0f,
      0f,
      bgBitmap.height.toFloat(),
      darkColor,
      lightColor,
      Shader.TileMode.CLAMP
    )
    val paint = Paint().apply {
      shader = gradient
    }
    val rectF = RectF(0f, 0f, bgBitmap.width.toFloat(), bgBitmap.height.toFloat())

    canvas.drawRect(rectF, paint)
    Glide.with(context.applicationContext)
      .load(bgBitmap)
      .centerCrop()
      .diskCacheStrategy(DiskCacheStrategy.NONE)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(view)
  }

  /**
   * Tint the menu icon
   *
   * @param context context
   * @param item    a menu item
   * @param color   color
   */
  fun tintMenuIcon(context: Context, item: MenuItem?, @ColorRes color: Int) {
    if (item == null) {
      return
    }
    val normalDrawable = item.icon ?: return
    val wrapDrawable = DrawableCompat.wrap(normalDrawable)
    DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
    item.icon = wrapDrawable
  }

  fun tintToolbarIcon(context: Context, menu: Menu, toggle: ActionBarDrawerToggle?, type: String) {
    val colorRes: Int = if (type == Const.ACTION_BAR_TYPE_DARK) {
      R.color.black
    } else {
      R.color.white
    }
    menu.children.forEach { tintMenuIcon(context, it, colorRes) }

    toggle?.let {
      if (type == Const.ACTION_BAR_TYPE_DARK) {
        it.drawerArrowDrawable.color = Color.BLACK
      } else {
        it.drawerArrowDrawable.color = Color.WHITE
      }
    }
  }
}
