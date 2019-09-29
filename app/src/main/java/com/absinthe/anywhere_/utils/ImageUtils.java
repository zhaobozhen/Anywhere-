package com.absinthe.anywhere_.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ImageUtils {

    public static Drawable getAppIconByPackageName(Context mContext, String apkTempPackageName){
        Drawable drawable;
        try{
            drawable = mContext.getPackageManager().getApplicationIcon(apkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher);
        }
        return drawable;
    }

    public static void setActionBarTransparent(Activity activity) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        Window window = activity.getWindow();

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.transparent)));
        }
        window.setStatusBarColor(activity.getResources().getColor(R.color.transparent));
    }

    /**
     * Load custom background pic
     * @param context Context for use Glide
     * @param imageView Load pic in this view
     */
    public static void loadBackgroundPic(Context context, ImageView imageView) {
        String backgroundUri = SPUtils.getString(context, ConstUtil.SP_KEY_CHANGE_BACKGROUND);
        if (!backgroundUri.isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(backgroundUri))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView);
        }
    }

    /**
     * Get Display
     * @param context Context for get WindowManager
     * @return Display
     */
    public static Display getDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            return wm.getDefaultDisplay();
        } else {
            return null;
        }
    }

}
