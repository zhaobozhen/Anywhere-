package com.absinthe.anywhere_.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Calendar;
import java.util.List;

public class UiUtils {

    /**
     * Get app icon by package name
     *
     * @param context for get manager
     * @param item    for get package name
     */
    public static Drawable getAppIconByPackageName(Context context, AnywhereEntity item) {
        int type = item.getAnywhereType();
        String apkTempPackageName = "";

        switch (type) {
            case AnywhereType.URL_SCHEME:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getParam1()));
                List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo.size() != 0) {
                    apkTempPackageName = resolveInfo.get(0).activityInfo.packageName;
                }
                break;
            case AnywhereType.ACTIVITY:
                apkTempPackageName = item.getParam1();
            case AnywhereType.MINI_PROGRAM:
                //Todo
                break;
        }

        Drawable drawable;
        try {
            if (GlobalValues.sIconPack.equals(Settings.DEFAULT_ICON_PACK) || GlobalValues.sIconPack.isEmpty() || Settings.sIconPack == null) {
                drawable = context.getPackageManager().getApplicationIcon(apkTempPackageName);
            } else {
                drawable = Settings.sIconPack.getDrawableIconForPackage(apkTempPackageName, context.getPackageManager().getApplicationIcon(apkTempPackageName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_logo);
        }
        return drawable;
    }

    public static Drawable getAppIconByPackageName(Context context, String packageName) {
        Drawable drawable;
        try {
            if (GlobalValues.sIconPack.equals(Settings.DEFAULT_ICON_PACK) || GlobalValues.sIconPack.isEmpty() || Settings.sIconPack == null) {
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
     * Set action bar title
     *
     * @param activity  Activity for bind action bar
     * @param actionBar our target
     */
    public static void setActionBarTitle(AppCompatActivity activity, ActionBar actionBar) {
        LogUtil.d("setActionBarTitle:workingMode =", GlobalValues.sWorkingMode);
        switch (GlobalValues.sWorkingMode) {
            case "":
                setTopWidgetColor(activity, actionBar, GlobalValues.sActionBarType, "Nowhere-");
                break;
            case Const.WORKING_MODE_URL_SCHEME:
                setTopWidgetColor(activity, actionBar, GlobalValues.sActionBarType, "Somewhere-");
                break;
            case Const.WORKING_MODE_ROOT:
            case Const.WORKING_MODE_SHIZUKU:
                setTopWidgetColor(activity, actionBar, GlobalValues.sActionBarType, "Anywhere-");
                break;
            default:
        }
    }

    /**
     * get action bar title by working mode
     *
     * @return action bar title
     */
    public static String getActionBarTitle() {
        StringBuilder title = new StringBuilder();

        switch (GlobalValues.sWorkingMode) {
            case "":
                title.append("Nowhere-");
            case Const.WORKING_MODE_URL_SCHEME:
                title.append("Somewhere-");
            case Const.WORKING_MODE_ROOT:
            case Const.WORKING_MODE_SHIZUKU:
            default:
                title.append("Anywhere-");
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

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

    }

    /**
     * Load custom background pic
     *
     * @param context   Context for use Glide
     * @param imageView Load pic in this view
     */
    public static void loadBackgroundPic(Context context, ImageView imageView) {
        if (!GlobalValues.sBackgroundUri.isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(GlobalValues.sBackgroundUri))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView);
        }
    }

    /**
     * Judge that the action bar title color should be
     *
     * @param activity Activity for use Glide
     */
    public static void setAdaptiveActionBarTitleColor(AppCompatActivity activity, ActionBar actionBar, String title) {
        if (GlobalValues.sBackgroundUri.isEmpty()) {
            return;
        }

        if (!GlobalValues.sActionBarType.isEmpty()) {
            setTopWidgetColor(activity, actionBar, GlobalValues.sActionBarType, title);
            return;
        }

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();

        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            LogUtil.d("actionBarHeight = " + actionBarHeight);
        }

        int finalActionBarHeight = actionBarHeight;
        Glide.with(activity)
                .asBitmap()
                .load(Uri.parse(GlobalValues.sBackgroundUri))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap actionBarBitmap = Bitmap.createBitmap(resource, 0, 0,
                                resource.getWidth(), Math.min(finalActionBarHeight, resource.getHeight()));
                        LogUtil.d("actionBarBitmap.getWidth() =", actionBarBitmap.getWidth());
                        LogUtil.d("actionBarBitmap.getHeight() =", actionBarBitmap.getHeight());

                        Palette.from(actionBarBitmap).generate(p -> {
                            if (p != null) {
                                //主导颜色,如果分析不出来，则返回默认颜色
                                int dominantColor = p.getDominantColor(activity.getResources().getColor(R.color.colorPrimary));

                                //RGB 转化为 YUV 计算颜色灰阶判断深浅
                                int grayScale = (int) (Color.red(dominantColor) * 0.299 + Color.green(dominantColor) * 0.587 + Color.blue(dominantColor) * 0.114);
                                if (grayScale > 192) {
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
     * @param view card view
     * @param drawable icon drawable
     */
    public static void setCardUseIconColor(View view, Drawable drawable) {
        Palette.from(drawableToBitmap(drawable)).generate(p -> {
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
        if (GlobalValues.sBackgroundUri.isEmpty() && type.equals(Const.ACTION_BAR_TYPE_LIGHT)) {
            GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_DARK);
            type = Const.ACTION_BAR_TYPE_DARK;
        }

        if (type.equals(Const.ACTION_BAR_TYPE_DARK) || type.isEmpty()) {
            LogUtil.d("Dark-");
            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLACK);

            if (isDarkMode(activity)) {
                span = new ForegroundColorSpan(Color.WHITE);
            }

            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_DARK);
            activity.invalidateOptionsMenu();

            if (isDarkMode(activity)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        } else if (type.equals(Const.ACTION_BAR_TYPE_LIGHT)) {
            LogUtil.d("Light-");
            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_LIGHT);
            activity.invalidateOptionsMenu();

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * Judge that whether open the dark mode
     *
     * @return MODE_NIGHT_YES or MODE_NIGHT_NO
     */
    public static int getAutoDarkMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        LogUtil.d("Current hour =", hour);

        if (hour >= 22 || hour <= 7) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
    }

    /**
     * transform drawable object to a bitmap object
     *
     * @param drawable our target
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
    public static int dipToPixels(Context context, float dipValue) {
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
    private static void createLinearGradientBitmap(ImageView view, int darkColor, int color) {
        int[] bgColors = new int[2];
        bgColors[0] = darkColor;
        bgColors[1] = color;

        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
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
        Glide.with(AnywhereApplication.sContext)
                .load(bgBitmap)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    /**
     * Clear light status bar state
     *
     * @param view decor view
     */
    public static void clearLightStatusBarAndNavigationBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            view.setSystemUiVisibility(flags);
        }
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
}
