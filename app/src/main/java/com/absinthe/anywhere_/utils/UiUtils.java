package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Calendar;
import java.util.List;

public class UiUtils {
    private static final Class klass = UiUtils.class;

    /**
     * Get app icon by package name
     * @param context for get manager
     * @param item for get package name
     */
    public static Drawable getAppIconByPackageName(Context context, AnywhereEntity item){
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
        try{
            drawable = context.getPackageManager().getApplicationIcon(apkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_logo);
        }
        return drawable;
    }

    /**
     * Set action bar title
     * @param activity Activity for bind action bar
     * @param actionBar our target
     */
    public static void setActionBarTitle(Activity activity, ActionBar actionBar) {
        LogUtil.d(klass, "setActionBarTitle:workingMode =", GlobalValues.sWorkingMode);
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
     */
    public static String getActionBarTitle() {
        switch (GlobalValues.sWorkingMode) {
            case "":
                return "Nowhere-";
            case Const.WORKING_MODE_URL_SCHEME:
                return "Somewhere-";
            case Const.WORKING_MODE_ROOT:
            case Const.WORKING_MODE_SHIZUKU:
                return "Anywhere-";
            default:
        }
        return "Anywhere-";
    }

    /**
     * Set action bar style
     * @param activity Activity for bind action bar
     */
    public static void setActionBarTransparent(Activity activity) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        Window window = activity.getWindow();
        int transparent = activity.getResources().getColor(R.color.transparent);

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(transparent));
        }
        window.setStatusBarColor(transparent);
        window.setNavigationBarColor(transparent);
    }

    /**
     * Reset action bar style
     * @param activity Activity for bind action bar
     */
    public static void resetActionBar(Activity activity) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        Window window = activity.getWindow();

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.resetColorPrimary)));
        }
        window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     * Load custom background pic
     * @param context Context for use Glide
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
     * @param activity Activity for use Glide
     */
    public static void setAdaptiveActionBarTitleColor(Activity activity, ActionBar actionBar, String title) {
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
            LogUtil.d(klass, "actionBarHeight = " + actionBarHeight);
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
                        LogUtil.d(klass, "actionBarBitmap.getWidth() =", actionBarBitmap.getWidth());
                        LogUtil.d(klass, "actionBarBitmap.getHeight() =", actionBarBitmap.getHeight());

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
     * Set action bar title color and status bar and navigation bar style
     * @param activity Activity for bind action bar
     * @param actionBar our target
     * @param type dark or light
     * @param title action bar title
     */
    private static void setTopWidgetColor(Activity activity, ActionBar actionBar, String type, String title) {

        if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
            LogUtil.d(klass, "Dark-");
            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLACK);
            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_DARK);
            activity.invalidateOptionsMenu();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (type.equals(Const.ACTION_BAR_TYPE_LIGHT) || type.isEmpty()) {
            LogUtil.d(klass, "Light-");
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
     */
    public static int getAutoDarkMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        LogUtil.d(klass, "Current hour =", hour);

        if (hour >= 22 || hour <= 7) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
    }

    /**
     * transform drawable object to a bitmap object
     * @param drawable our target
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
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
     * @param view our target
     * @param trueIsVisible if is true that set visible
     */
    public static void setVisibility(@NonNull View view, boolean trueIsVisible) {
        view.setVisibility(trueIsVisible ? View.VISIBLE : View.GONE);
    }
}
