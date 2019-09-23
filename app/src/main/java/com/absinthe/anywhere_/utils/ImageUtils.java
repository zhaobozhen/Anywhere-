package com.absinthe.anywhere_.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.absinthe.anywhere_.R;

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
}
