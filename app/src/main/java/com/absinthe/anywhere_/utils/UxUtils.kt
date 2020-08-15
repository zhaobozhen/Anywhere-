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
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
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
import com.absinthe.anywhere_.constants.GlobalValues.isMd2Toolbar
import com.absinthe.anywhere_.constants.GlobalValues.isPages
import com.absinthe.anywhere_.constants.GlobalValues.workingMode
import com.absinthe.anywhere_.listener.OnPaletteFinishedListener
import com.absinthe.anywhere_.model.Settings
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.AppUtils.getPackageNameByScheme
import com.absinthe.anywhere_.utils.StatusBarUtil.clearLightStatusBarAndNavigationBar
import com.absinthe.anywhere_.utils.manager.ShadowHelper
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    fun getAppIcon(context: Context, item: AnywhereEntity): Drawable {
        val packageName: String
        when (item.type) {
            AnywhereType.Card.URL_SCHEME -> {
                packageName = if (TextUtils.isEmpty(item.param2)) {
                    getPackageNameByScheme(context, item.param1)
                } else {
                    item.packageName
                }
                return getAppIcon(context, packageName)
            }
            AnywhereType.Card.ACTIVITY, AnywhereType.Card.QR_CODE -> {
                return getAppIcon(context, item.packageName)
            }
            AnywhereType.Card.IMAGE -> return context.getDrawable(R.drawable.ic_card_image)!!
            AnywhereType.Card.SHELL -> return context.getDrawable(R.drawable.ic_card_shell)!!
            AnywhereType.Card.SWITCH_SHELL -> return context.getDrawable(R.drawable.ic_card_switch)!!
            AnywhereType.Card.FILE -> return context.getDrawable(R.drawable.ic_card_file)!!
            AnywhereType.Card.BROADCAST -> return context.getDrawable(R.drawable.ic_card_broadcast)!!
        }
        return context.getDrawable(R.drawable.ic_logo)!!
    }

    fun getAppIcon(context: Context, item: AppListBean): Drawable {
        val ae = AnywhereEntity.Builder()
        if (item.type == AnywhereType.Card.URL_SCHEME || item.type == AnywhereType.Card.IMAGE || item.type == AnywhereType.Card.SHELL) {
            ae.param1 = item.className
            ae.param2 = item.packageName
        } else {
            ae.param1 = item.packageName
            ae.param2 = item.className
        }
        ae.type = item.type
        return getAppIcon(context, ae)
    }

    fun getAppIcon(context: Context, packageName: String): Drawable {
        val drawable: Drawable
        drawable = try {
            if (iconPack == Const.DEFAULT_ICON_PACK || iconPack.isEmpty()) {
                context.packageManager.getApplicationIcon(packageName)
            } else {
                Settings.sIconPack?.getDrawableIconForPackage(packageName, context.packageManager.getApplicationIcon(packageName))
                        ?: context.getDrawable(R.drawable.ic_logo)!!
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            context.getDrawable(R.drawable.ic_logo)!!
        }
        return drawable
    }

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

        if (Settings.sDate == "12-25") {
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
        activity.supportActionBar?.let {
            if (!isMd2Toolbar) {
                it.setBackgroundDrawable(null)
            }
        }
        StatusBarUtil.setSystemBarTransparent(activity)
    }

    /**
     * Judge that whether is light color
     *
     * @param color target color
     * @return true if is light color
     */
    fun isLightColor(color: Int): Boolean {
        //RGB 转化为 YUV 计算颜色灰阶判断深浅
        val grayScale = (Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114).toInt()
        return grayScale >= 192
    }

    /**
     * Judge that the action bar title color should be
     *
     * @param activity Activity for use Glide
     */
    fun setAdaptiveToolbarTitleColor(activity: AppCompatActivity, toolbar: Toolbar) {
        if (backgroundUri.isEmpty()) {
            return
        }

        val title = getToolbarTitle()
        if (actionBarType.isNotEmpty()) {
            setTopWidgetColor(activity, toolbar, actionBarType, title)
            return
        }

        Glide.with(activity)
                .asBitmap()
                .load(Uri.parse(backgroundUri))
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        val actionBarBitmap = Bitmap.createBitmap(resource, 0, 0,
                                resource.width, BarUtils.getActionBarHeight().coerceAtMost(resource.height))

                        Palette.from(actionBarBitmap).generate { p: Palette? ->
                            if (p != null) {
                                //主导颜色,如果分析不出来，则返回默认颜色
                                val dominantColor = p.getDominantColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                                if (isLightColor(dominantColor)) {
                                    // 深色字体
                                    setTopWidgetColor(activity, toolbar, Const.ACTION_BAR_TYPE_DARK, title)
                                } else {
                                    // 浅色字体
                                    setTopWidgetColor(activity, toolbar, Const.ACTION_BAR_TYPE_LIGHT, title)
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
     * @param toolbar our target
     * @param type      dark or light
     * @param title     action bar title
     */
    private fun setTopWidgetColor(activity: AppCompatActivity, toolbar: Toolbar, type: String, title: String) {
        var newType = type

        if (backgroundUri.isEmpty() && type == Const.ACTION_BAR_TYPE_LIGHT) {
            actionBarType = Const.ACTION_BAR_TYPE_DARK
            newType = Const.ACTION_BAR_TYPE_DARK
        }
        if (newType == Const.ACTION_BAR_TYPE_DARK || newType.isEmpty()) {
            Timber.d("Dark-")
            val span = if (StatusBarUtil.isDarkMode(activity) && backgroundUri.isEmpty()) {
                ForegroundColorSpan(Color.WHITE)
            } else {
                ForegroundColorSpan(Color.BLACK)
            }
            toolbar.title = SpannableString(title).apply {
                setSpan(span, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            toolbar.setTitleTextColor(Color.BLACK)

            actionBarType = Const.ACTION_BAR_TYPE_DARK
            activity.invalidateOptionsMenu()
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    activity.window.decorView.systemUiVisibility

            if (backgroundUri.isNotEmpty() || isPages) {
                setActionBarTransparent(activity)
            }
        } else if (newType == Const.ACTION_BAR_TYPE_LIGHT) {
            Timber.d("Light-")
            val span = ForegroundColorSpan(Color.WHITE)
            toolbar.title = SpannableString(title).apply {
                setSpan(span, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            toolbar.setTitleTextColor(Color.WHITE)

            actionBarType = Const.ACTION_BAR_TYPE_LIGHT

            activity.invalidateOptionsMenu()
            clearLightStatusBarAndNavigationBar(activity.window.decorView)
        }
    }

    /**
     * Make the card use icon's color
     *
     * @param cardBackgroundView     card view
     * @param drawable icon drawable
     * @param listener notify when Palette finished
     */
    fun setCardUseIconColor(cardBackgroundView: View, drawable: Drawable, listener: OnPaletteFinishedListener) {
        val bitmap = ConvertUtils.drawable2Bitmap(drawable) ?: return

        Palette.from(bitmap).generate { p: Palette? ->
            if (p != null) {
                var color = p.getVibrantColor(Color.TRANSPARENT)
                if (color == Color.TRANSPARENT) {
                    color = p.getDominantColor(Color.TRANSPARENT)
                }
                cardBackgroundView.setBackgroundColor(color)
                listener.onFinished(color)
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
                        || hour < endHour || hour == endHour && minute <= endMinute) {
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
     * @param activity activity
     * @param view      target to set background
     * @param darkColor primary color
     */
    suspend fun createLinearGradientBitmap(activity: BaseActivity, view: ImageView, darkColor: Int) {
        var w = view.width
        var h = view.height

        while (true) {
            delay(50)
            if (view.width > 0 && view.height > 0 && w == view.width && h == view.height) {
                break
            } else {
                w = view.width
                h = view.height
            }
        }

        val bgBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas().apply {
            setBitmap(bgBitmap)
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }
        val lightColor = darkColor and 0x00ffffff or (128 shl 24)
        val gradient = LinearGradient(0f, 0f, 0f, bgBitmap.height.toFloat(), darkColor, lightColor, Shader.TileMode.CLAMP)
        val paint = Paint().apply {
            shader = gradient
        }
        val rectF = RectF(0f, 0f, bgBitmap.width.toFloat(), bgBitmap.height.toFloat())

        canvas.drawRect(rectF, paint)
        withContext(Dispatchers.Main) {
            Glide.with(activity)
                    .load(bgBitmap)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
        }
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
        val normalDrawable = item.icon
        val wrapDrawable = DrawableCompat.wrap(normalDrawable)
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
        item.icon = wrapDrawable
    }

    fun tintToolbarIcon(context: Context, menu: Menu, toggle: ActionBarDrawerToggle, type: String) {
        val colorRes: Int = if (type == Const.ACTION_BAR_TYPE_DARK) {
            R.color.black
        } else {
            R.color.white
        }
        tintMenuIcon(context, menu.findItem(R.id.toolbar_settings), colorRes)
        tintMenuIcon(context, menu.findItem(R.id.toolbar_sort), colorRes)
        tintMenuIcon(context, menu.findItem(R.id.toolbar_delete), colorRes)
        tintMenuIcon(context, menu.findItem(R.id.toolbar_done), colorRes)
        tintMenuIcon(context, menu.findItem(R.id.toolbar_done), colorRes)
        if (type == Const.ACTION_BAR_TYPE_DARK) {
            toggle.drawerArrowDrawable.color = Color.BLACK
        } else {
            toggle.drawerArrowDrawable.color = Color.WHITE
        }
    }

    fun drawMd2Toolbar(toolbar: Toolbar, shadowRadius: Int) {
        ShadowHelper.getInstance()
                .setShape(ShadowHelper.SHAPE_ROUND)
                .setShapeRadius(ConvertUtils.dp2px(8f))
                .setShadowRadius(ConvertUtils.dp2px(shadowRadius.toFloat()))
                .setShadowColor(Color.parseColor("#4D000000"))
                .into(toolbar)
    }
}