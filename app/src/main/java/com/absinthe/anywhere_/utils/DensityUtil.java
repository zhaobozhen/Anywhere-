package com.absinthe.anywhere_.utils;

import android.content.Context;

/**
 * 密度转换工具类
 */
public class DensityUtil {
    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param density 密度
     * @param dpValue dp
     * @return px
     */
    public static int dip2px(final float density, final float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param density 密度
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(final float density, final float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }

    /**
     *
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context 上下文
     * @param dpValue dp
     * @return px
     */
    public static int dip2px(Context context, final float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context 上下文
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(Context context, final float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

}
