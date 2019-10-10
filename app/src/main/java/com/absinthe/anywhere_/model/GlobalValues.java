package com.absinthe.anywhere_.model;

import android.content.Context;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.SPUtils;

public class GlobalValues {
    public static boolean sIsFirstLaunch;

    public static String sWorkingMode;
    public static String sActionBarTitle;
    public static String sActionBarType;
    public static String sDarkMode;

    public static void init(Context context) {
        sIsFirstLaunch = SPUtils.getBoolean(context, ConstUtil.SP_KEY_FIRST_LAUNCH);
        sWorkingMode = SPUtils.getString(context, ConstUtil.SP_KEY_WORKING_MODE);
        sActionBarTitle = SPUtils.getString(context, ConstUtil.SP_KEY_ACTION_BAR_TITLE);
        sActionBarType = SPUtils.getString(context, ConstUtil.SP_KEY_ACTION_BAR_TYPE);
        sDarkMode = SPUtils.getString(context, ConstUtil.SP_KEY_DARK_MODE);
    }

    public static void setsIsFirstLaunch(boolean sIsFirstLaunch) {
        GlobalValues.sIsFirstLaunch = sIsFirstLaunch;
        SPUtils.putBoolean(AnywhereApplication.sContext, ConstUtil.SP_KEY_FIRST_LAUNCH, sIsFirstLaunch);
    }

    public static void setsWorkingMode(String sWorkingMode) {
        GlobalValues.sWorkingMode = sWorkingMode;
        SPUtils.putString(AnywhereApplication.sContext, ConstUtil.SP_KEY_WORKING_MODE, sWorkingMode);
    }

    public static void setsActionBarTitle(String sActionBarTitle) {
        GlobalValues.sActionBarTitle = sActionBarTitle;
        SPUtils.putString(AnywhereApplication.sContext, ConstUtil.SP_KEY_ACTION_BAR_TITLE, sActionBarTitle);
    }

    public static void setsActionBarType(String sActionBarType) {
        GlobalValues.sActionBarType = sActionBarType;
        SPUtils.putString(AnywhereApplication.sContext, ConstUtil.SP_KEY_ACTION_BAR_TYPE, sActionBarType);
    }

    public static void setsDarkMode(String sDarkMode) {
        GlobalValues.sDarkMode = sDarkMode;
        SPUtils.putString(AnywhereApplication.sContext, ConstUtil.SP_KEY_DARK_MODE, sDarkMode);
    }
}
