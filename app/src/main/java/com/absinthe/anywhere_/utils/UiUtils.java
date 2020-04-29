package com.absinthe.anywhere_.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.interfaces.OnPaletteFinishedListener;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.constants.Const;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler;
import com.absinthe.anywhere_.utils.manager.ShadowHelper;
import com.absinthe.anywhere_.view.editor.SwitchShellEditor;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class UiUtils {
    /**
     * Get package name by url scheme
     *
     * @param context for get manager
     * @param url     for get package name
     */
    public static String getPkgNameByUrl(Context context, String url) {
        String apkTempPackageName = "";
        List<ResolveInfo> resolveInfo =
                context.getPackageManager()
                        .queryIntentActivities(URLSchemeHandler.handleIntent(url), PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() != 0) {
            apkTempPackageName = resolveInfo.get(0).activityInfo.packageName;
        }
        return apkTempPackageName;
    }

    /**
     * Get app icon by package name
     *
     * @param context for get manager
     * @param item    for get package name
     */
    public static Drawable getAppIconByPackageName(Context context, AnywhereEntity item) {
        int type = item.getAnywhereType();
        String apkTempPackageName;

        switch (type) {
            case AnywhereType.URL_SCHEME:
                if (TextUtils.isEmpty(item.getParam2())) {
                    apkTempPackageName = getPkgNameByUrl(context, item.getParam1());
                } else {
                    apkTempPackageName = item.getParam2();
                }
                return getAppIconByPackageName(context, apkTempPackageName);
            case AnywhereType.ACTIVITY:
            case AnywhereType.QR_CODE:
                apkTempPackageName = item.getParam1();
                return getAppIconByPackageName(context, apkTempPackageName);
            case AnywhereType.IMAGE:
                return context.getDrawable(R.drawable.ic_photo);
            case AnywhereType.SHELL:
                return context.getDrawable(R.drawable.ic_code);
            case AnywhereType.SWITCH_SHELL:
                if (item.getParam3().equals(SwitchShellEditor.SWITCH_SHELL_OFF_STATUS))
                    return context.getDrawable(R.drawable.ic_switch_off);
                else
                    return context.getDrawable(R.drawable.ic_switch_on);
        }

        return context.getDrawable(R.drawable.ic_logo);
    }

    public static Drawable getAppIconByPackageName(Context context, AppListBean item) {
        AnywhereEntity ae = AnywhereEntity.Builder();
        if (item.getType() == AnywhereType.URL_SCHEME
                || item.getType() == AnywhereType.IMAGE
                || item.getType() == AnywhereType.SHELL) {
            ae.setParam1(item.getClassName());
            ae.setParam2(item.getPackageName());
        } else {
            ae.setParam1(item.getPackageName());
            ae.setParam2(item.getClassName());
        }
        ae.setType(item.getType());
        return getAppIconByPackageName(context, ae);
    }

    public static Drawable getAppIconByPackageName(Context context, String packageName) {
        Drawable drawable;
        try {
            if (GlobalValues.INSTANCE.getIconPack().equals(Const.DEFAULT_ICON_PACK) || GlobalValues.INSTANCE.getIconPack().isEmpty()) {
                drawable = context.getPackageManager().getApplicationIcon(packageName);
            } else {
                drawable = Settings.sIconPack.getDrawableIconForPackage(packageName, context.getPackageManager().getApplicationIcon(packageName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_logo);
        }
        return drawable;
    }

    /**
     * Get activity icon
     *
     * @param context context
     * @param cn      componentName
     * @return activity icon
     */
    public static Drawable getActivityIcon(Context context, ComponentName cn) {
        Drawable drawable;
        try {
            drawable = context.getPackageManager().getActivityIcon(cn);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_logo);
        }
        return drawable;
    }

    /**
     * Get activity label name
     *
     * @param context context
     * @param cn      componentName
     * @return activity label name
     */
    public static String getActivityLabel(Context context, ComponentName cn) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ActivityInfo info = packageManager.getActivityInfo(cn, 0);
            return info.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Judge that whether an activity is exported
     *
     * @param context context
     * @param cn      componentName
     * @return true if the activity is exported
     */
    public static boolean isActivityExported(Context context, ComponentName cn) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ActivityInfo info = packageManager.getActivityInfo(cn, 0);
            return info.exported;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get action bar title by working mode
     *
     * @return action bar title
     */
    public static String getActionBarTitle() {
        StringBuilder title = new StringBuilder();

        switch (GlobalValues.INSTANCE.getWorkingMode()) {
            case "":
                title.append(AnywhereType.NOWHERE);
                break;
            case Const.WORKING_MODE_URL_SCHEME:
                title.append(AnywhereType.SOMEWHERE);
                break;
            case Const.WORKING_MODE_ROOT:
            case Const.WORKING_MODE_SHIZUKU:
            default:
                title.append(AnywhereType.ANYWHERE);
                break;
        }

        if (Settings.sDate.equals("12-25")) {
            title.append(" \uD83C\uDF84");
            Timber.d("title = %s", title);
        } else if (Settings.sDate.equals("01-25")) {
            title.append(" \uD83D\uDC2D");
        }

        return title.toString();
    }

    /**
     * Set action bar style
     *
     * @param activity Activity for bind action bar
     */
    public static void setActionBarTransparent(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        Window window = activity.getWindow();
        View view = window.getDecorView();

        if (actionBar != null && !GlobalValues.INSTANCE.isMd2Toolbar()) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        int flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        view.setSystemUiVisibility(view.getSystemUiVisibility() | flag);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    /**
     * Judge that whether is light color
     *
     * @param color target color
     * @return true if is light color
     */
    public static boolean isLightColor(int color) {
        //RGB 转化为 YUV 计算颜色灰阶判断深浅
        int grayScale = (int) (Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114);
        return grayScale >= 192;
    }

    /**
     * Judge that the action bar title color should be
     *
     * @param activity Activity for use Glide
     */
    public static void setAdaptiveActionBarTitleColor(AppCompatActivity activity, ActionBar actionBar, String title) {
        if (GlobalValues.INSTANCE.getBackgroundUri().isEmpty()) {
            return;
        }

        if (!GlobalValues.INSTANCE.getActionBarType().isEmpty()) {
            setTopWidgetColor(activity, actionBar, GlobalValues.INSTANCE.getActionBarType(), title);
            return;
        }

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();

        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            Timber.d("actionBarHeight = %d", actionBarHeight);
        }

        final int finalActionBarHeight = actionBarHeight;
        Glide.with(activity)
                .asBitmap()
                .load(Uri.parse(GlobalValues.INSTANCE.getBackgroundUri()))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap actionBarBitmap = Bitmap.createBitmap(resource, 0, 0,
                                resource.getWidth(), Math.min(finalActionBarHeight, resource.getHeight()));
                        Timber.d("actionBarBitmap.getWidth() = %d", actionBarBitmap.getWidth());
                        Timber.d("actionBarBitmap.getHeight() = %d", actionBarBitmap.getHeight());

                        Palette.from(actionBarBitmap).generate(p -> {
                            if (p != null) {
                                //主导颜色,如果分析不出来，则返回默认颜色
                                int dominantColor = p.getDominantColor(activity.getResources().getColor(R.color.colorPrimary));

                                if (isLightColor(dominantColor)) {
                                    // 深色字体
                                    setTopWidgetColor(activity, actionBar, Const.ACTION_BAR_TYPE_DARK, title);
                                } else {
                                    // 浅色字体
                                    setTopWidgetColor(activity, actionBar, Const.ACTION_BAR_TYPE_LIGHT, title);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

    }

    /**
     * Make the card use icon's color
     *
     * @param view     card view
     * @param drawable icon drawable
     * @param listener notify when Palette finished
     */
    public static void setCardUseIconColor(View view, Drawable drawable, OnPaletteFinishedListener listener) {
        if (drawable == null) {
            return;
        }

        Bitmap bitmap = ConvertUtils.drawable2Bitmap(drawable);
        if (bitmap == null) {
            return;
        }

        Palette.from(bitmap).generate(p -> {
            if (p != null) {
                int color = p.getVibrantColor(Color.TRANSPARENT);
                if (color == Color.TRANSPARENT) {
                    color = p.getDominantColor(Color.TRANSPARENT);
                }

                view.setBackgroundColor(color);
                listener.onFinished(color);
            }
        });
    }

    /**
     * Make the card use icon's color
     *
     * @param view     card view
     * @param drawable icon drawable
     */
    public static void setCardUseIconColor(View view, Drawable drawable) {
        if (drawable == null) {
            return;
        }

        Bitmap bitmap = ConvertUtils.drawable2Bitmap(drawable);
        if (bitmap == null) {
            return;
        }

        Palette.from(bitmap).generate(p -> {
            if (p != null) {
                if (p.getVibrantColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
                    createLinearGradientBitmap((ImageView) view, p.getVibrantColor(Color.TRANSPARENT), Color.TRANSPARENT);
                } else {
                    createLinearGradientBitmap((ImageView) view, p.getDominantColor(Color.TRANSPARENT), Color.TRANSPARENT);
                }
            }
        });
    }

    /**
     * Set action bar title color and status bar and navigation bar style
     *
     * @param activity  Activity for bind action bar
     * @param actionBar our target
     * @param type      dark or light
     * @param title     action bar title
     */
    private static void setTopWidgetColor(AppCompatActivity activity, ActionBar actionBar, String type, String title) {
        if (GlobalValues.INSTANCE.getBackgroundUri().isEmpty() && type.equals(Const.ACTION_BAR_TYPE_LIGHT)) {
            GlobalValues.INSTANCE.setActionBarType(Const.ACTION_BAR_TYPE_DARK);
            type = Const.ACTION_BAR_TYPE_DARK;
        }

        if (type.equals(Const.ACTION_BAR_TYPE_DARK) || type.isEmpty()) {
            Timber.d("Dark-");

            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLACK);

            if (isDarkMode(activity) && GlobalValues.INSTANCE.getBackgroundUri().isEmpty()) {
                span = new ForegroundColorSpan(Color.WHITE);
            }

            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.INSTANCE.setActionBarType(Const.ACTION_BAR_TYPE_DARK);
            activity.invalidateOptionsMenu();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                                activity.getWindow().getDecorView().getSystemUiVisibility());
            }

            if (!GlobalValues.INSTANCE.getBackgroundUri().isEmpty() || GlobalValues.INSTANCE.isPages()) {
                setActionBarTransparent(activity);
            }
        } else if (type.equals(Const.ACTION_BAR_TYPE_LIGHT)) {
            Timber.d("Light-");
            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.INSTANCE.setActionBarType(Const.ACTION_BAR_TYPE_LIGHT);
            activity.invalidateOptionsMenu();

            StatusBarUtil.clearLightStatusBarAndNavigationBar(activity.getWindow().getDecorView());
        }
    }

    /**
     * Judge that whether open the dark mode
     *
     * @return MODE_NIGHT_YES or MODE_NIGHT_NO
     */
    public static int getAutoDarkMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(GlobalValues.INSTANCE.getAutoDarkModeStart());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(GlobalValues.INSTANCE.getAutoDarkModeEnd());

        int startHour = 22;
        int startMinute = 0;
        if (GlobalValues.INSTANCE.getAutoDarkModeStart() != 0) {
            startHour = start.get(Calendar.HOUR_OF_DAY);
            startMinute = start.get(Calendar.MINUTE);
        }

        int endHour = 7;
        int endMinute = 0;
        if (GlobalValues.INSTANCE.getAutoDarkModeEnd() != 0) {
            endHour = end.get(Calendar.HOUR_OF_DAY);
            endMinute = end.get(Calendar.MINUTE);
        }

        if (startHour < endHour) {
            if ((hour >= startHour && minute >= startMinute) && (hour <= endHour && minute <= endMinute)) {
                return AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                return AppCompatDelegate.MODE_NIGHT_NO;
            }
        } else {
            if (startHour == endHour && startMinute < endMinute) {
                if (hour == startHour && minute >= startMinute && minute <= endMinute) {
                    return AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    return AppCompatDelegate.MODE_NIGHT_NO;
                }
            } else {
                if (hour > startHour
                        || (hour == startHour && minute >= startMinute)
                        || hour < endHour
                        || (hour == endHour && minute <= endMinute)) {
                    return AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    return AppCompatDelegate.MODE_NIGHT_NO;
                }
            }
        }
    }

    /**
     * Set visibility of a view
     *
     * @param view          our target
     * @param trueIsVisible if is true that set visible
     */
    public static void setVisibility(@NonNull View view, boolean trueIsVisible) {
        view.setVisibility(trueIsVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Convert dip to px
     *
     * @param context  to get resource
     * @param dipValue our target
     */
    public static int d2p(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    /**
     * Create a linear gradient bitmap picture
     *
     * @param view      target to set background
     * @param darkColor primary color
     * @param color     secondary color
     */
    public static void createLinearGradientBitmap(ImageView view, int darkColor, int color) {
        Timber.d("dark color = %d",darkColor);
        int[] bgColors = new int[2];
        bgColors[0] = darkColor;
        bgColors[1] = color;

        if (view == null) {
            return;
        } else if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> createLinearGradientBitmap(view, darkColor, color), 100);
            return;
        }

        Bitmap bgBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        Paint paint = new Paint();

        canvas.setBitmap(bgBitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        LinearGradient gradient = new LinearGradient(0, 0, 0, bgBitmap.getHeight(), bgColors[0], bgColors[1], Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        RectF rectF = new RectF(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
        canvas.drawRect(rectF, paint);
        Glide.with(Utils.getApp())
                .load(bgBitmap)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    /**
     * Judge that whether is dark mode
     *
     * @param context context
     * @return true if is dark mode
     */
    public static boolean isDarkMode(Context context) {
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            default:
                return false;
        }
    }

    /**
     * Tint the menu icon
     *
     * @param context context
     * @param item    a menu item
     * @param color   color
     */
    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        if (item == null) {
            return;
        }

        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }

    /**
     * Tint the menu icon with alpha
     *
     * @param context context
     * @param item    a menu item
     * @param color   color
     * @param alpha   alpha
     */
    public static void tintMenuIconWithAlpha(Context context, MenuItem item, @ColorRes int color, int alpha) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
        wrapDrawable.setAlpha(alpha);

        item.setIcon(wrapDrawable);
    }

    public static void tintToolbarIcon(Context context, Menu menu, ActionBarDrawerToggle mToggle, String type) {
        int colorRes;
        if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
            colorRes = R.color.black;
        } else {
            colorRes = R.color.white;
        }

        UiUtils.tintMenuIcon(context, menu.findItem(R.id.toolbar_settings), colorRes);
        UiUtils.tintMenuIcon(context, menu.findItem(R.id.toolbar_sort), colorRes);
        UiUtils.tintMenuIcon(context, menu.findItem(R.id.toolbar_delete), colorRes);
        UiUtils.tintMenuIcon(context, menu.findItem(R.id.toolbar_done), colorRes);
        UiUtils.tintMenuIcon(context, menu.findItem(R.id.toolbar_done), colorRes);

        if (mToggle != null) {
            if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
                mToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.black));
            } else {
                mToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.white));
            }
        }
    }

    public static void drawMd2Toolbar(Context context, Toolbar toolbar, int shadowRadius) {
        ShadowHelper.getInstance()
                .setShape(ShadowHelper.SHAPE_ROUND)
                .setShapeRadius(d2p(context, 8))
                .setShadowRadius(d2p(context, shadowRadius))
                .setShadowColor(Color.parseColor("#4D000000"))
                .into(toolbar);
    }
}
