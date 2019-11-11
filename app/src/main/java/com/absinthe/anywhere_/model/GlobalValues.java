package com.absinthe.anywhere_.model;

import android.content.Context;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.utils.SPUtils;

public class GlobalValues {
    public static boolean sIsDebugMode;
    public static boolean sIsFirstLaunch;
    public static boolean sIsStreamCardMode;
    public static boolean sIsStreamCardModeSingleLine;
    public static boolean sIsCardBackground;

    public static String sWorkingMode;
    public static String sActionBarTitle;
    public static String sActionBarType;
    public static String sDarkMode;
    public static String sBackgroundUri;
    public static String sSortMode;
    public static String sIconPack;

    public static void init(Context context) {
        sIsDebugMode = false;
        sIsFirstLaunch = SPUtils.getBoolean(context, Const.SP_KEY_FIRST_LAUNCH, true);
        sIsStreamCardMode = SPUtils.getBoolean(context, Const.SP_KEY_STREAM_CARD_MODE, false);
        sIsStreamCardModeSingleLine = SPUtils.getBoolean(context, Const.SP_KEY_STREAM_CARD_SINGLE_LINE, false);
        sIsCardBackground = SPUtils.getBoolean(context, Const.SP_KEY_CARD_BACKGROUND, true);
        sWorkingMode = SPUtils.getString(context, Const.SP_KEY_WORKING_MODE);
        sActionBarTitle = SPUtils.getString(context, Const.SP_KEY_ACTION_BAR_TITLE);
        sActionBarType = SPUtils.getString(context, Const.SP_KEY_ACTION_BAR_TYPE);
        sDarkMode = SPUtils.getString(context, Const.SP_KEY_DARK_MODE);
        sBackgroundUri = SPUtils.getString(context, Const.SP_KEY_CHANGE_BACKGROUND);
        sSortMode = SPUtils.getString(context, Const.SP_KEY_SORT_MODE);
        sIconPack = SPUtils.getString(context, Const.SP_KEY_ICON_PACK);
    }

    public static void setsIsFirstLaunch(boolean sIsFirstLaunch) {
        GlobalValues.sIsFirstLaunch = sIsFirstLaunch;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.SP_KEY_FIRST_LAUNCH, sIsFirstLaunch);
    }

    public static void setsIsStreamCardMode(boolean sIsStreamCardMode) {
        GlobalValues.sIsStreamCardMode = sIsStreamCardMode;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.SP_KEY_STREAM_CARD_MODE, sIsStreamCardMode);
    }

    public static void setsIsStreamCardModeSingleLine(boolean sIsStreamCardModeSingleLine) {
        GlobalValues.sIsStreamCardModeSingleLine = sIsStreamCardModeSingleLine;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.SP_KEY_STREAM_CARD_SINGLE_LINE, sIsStreamCardModeSingleLine);
    }

    public static void setsIsCardBackground(boolean sIsCardBackground) {
        GlobalValues.sIsCardBackground = sIsCardBackground;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.SP_KEY_CARD_BACKGROUND, sIsCardBackground);
    }

    public static void setsWorkingMode(String sWorkingMode) {
        GlobalValues.sWorkingMode = sWorkingMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_WORKING_MODE, sWorkingMode);
    }

    public static void setsActionBarTitle(String sActionBarTitle) {
        GlobalValues.sActionBarTitle = sActionBarTitle;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_ACTION_BAR_TITLE, sActionBarTitle);
    }

    public static void setsActionBarType(String sActionBarType) {
        GlobalValues.sActionBarType = sActionBarType;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_ACTION_BAR_TYPE, sActionBarType);
    }

    public static void setsDarkMode(String sDarkMode) {
        GlobalValues.sDarkMode = sDarkMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_DARK_MODE, sDarkMode);
    }

    public static void setsBackgroundUri(String sBackgroundUri) {
        GlobalValues.sBackgroundUri = sBackgroundUri;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_CHANGE_BACKGROUND, sBackgroundUri);
    }

    public static void setsSortMode(String sSortMode) {
        GlobalValues.sSortMode = sSortMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_SORT_MODE, sSortMode);
    }

    public static void setsIconPack(String sIconPack) {
        GlobalValues.sIconPack = sIconPack;
        SPUtils.putString(AnywhereApplication.sContext, Const.SP_KEY_ICON_PACK, sIconPack);
    }
}
