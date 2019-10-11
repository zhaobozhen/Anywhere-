package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static Drawable getAppIconByPackageName(Context mContext, AnywhereEntity item){
        int type = item.getType();
        String apkTempPackageName = "";

        switch (type) {
            case AnywhereType.URL_SCHEME:
                apkTempPackageName = item.getParam2() == null ? "" : item.getParam2();
                break;
            case AnywhereType.ACTIVITY:
                apkTempPackageName = item.getParam1();
            case AnywhereType.MINI_PROGRAM:
                //Todo
                break;
        }

        Drawable drawable;
        try{
            drawable = mContext.getPackageManager().getApplicationIcon(apkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_logo);
        }
        return drawable;
    }

    public static void setActionBarTitle(Activity activity, ActionBar actionBar) {
        Log.d(TAG, "setActionBarTitle:workingMode = " + GlobalValues.sWorkingMode);
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

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.transparent)));
        }
        window.setStatusBarColor(activity.getResources().getColor(R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(R.color.transparent));
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
        if (!GlobalValues.sActionBarType.isEmpty()) {
            setTopWidgetColor(activity, actionBar, GlobalValues.sActionBarType, title);
            return;
        }

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();

        if (GlobalValues.sBackgroundUri.isEmpty()) {
            return;
        }

        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            Log.d(TAG, "actionBarHeight = " + actionBarHeight);
        }

        int finalActionBarHeight = actionBarHeight;
        Glide.with(activity)
                .asBitmap()
                .load(Uri.parse(GlobalValues.sBackgroundUri))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap actionBarBitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), finalActionBarHeight);
                        Log.d(TAG, "actionBarBitmap.getWidth() = " + actionBarBitmap.getWidth());
                        Log.d(TAG, "actionBarBitmap.getHeight() = " + actionBarBitmap.getHeight());

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

    private static void setTopWidgetColor(Activity activity, ActionBar actionBar, String type, String title) {

        if (type.equals(Const.ACTION_BAR_TYPE_DARK)) {
            Log.d(TAG, "Dark-");
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
            Log.d(TAG, "Light-");
            SpannableString spanString = new SpannableString(title);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
            spanString.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spanString);

            GlobalValues.setsActionBarType(Const.ACTION_BAR_TYPE_LIGHT);
            activity.invalidateOptionsMenu();

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

}
