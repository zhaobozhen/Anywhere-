package com.absinthe.anywhere_.model;

import android.content.Context;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.utils.SPUtils;

public class GlobalValues {
    public static boolean sIsDebugMode;
    public static boolean sIsStreamCardMode;
    public static boolean sIsStreamCardModeSingleLine;
    public static boolean sIsMd2Toolbar;
    public static boolean sIsPages;
    public static boolean sIsCollectorPlus;

    public static String sWorkingMode;
    public static String sActionBarType;
    public static String sDarkMode;
    public static String sBackgroundUri;
    public static String sCardBackgroundMode;
    public static String sSortMode;
    public static String sIconPack;
    public static String sCategory;
    public static int sCurrentPage;
    public static int sDumpInterval;

    public static void init(Context context) {
        sIsDebugMode = false;
        sIsStreamCardMode = SPUtils.getBoolean(context, Const.PREF_STREAM_CARD_MODE, false);
        sIsStreamCardModeSingleLine = SPUtils.getBoolean(context, Const.PREF_STREAM_CARD_SINGLE_LINE, false);
        sIsMd2Toolbar = SPUtils.getBoolean(context, Const.PREF_MD2_TOOLBAR, false);
        sIsPages = SPUtils.getBoolean(context, Const.PREF_PAGES, false);
        sIsCollectorPlus = SPUtils.getBoolean(context, Const.PREF_COLLECTOR_PLUS, false);
        sWorkingMode = SPUtils.getString(context, Const.PREF_WORKING_MODE);
        sActionBarType = SPUtils.getString(context, Const.PREF_ACTION_BAR_TYPE);
        sDarkMode = SPUtils.getString(context, Const.PREF_DARK_MODE);
        sBackgroundUri = SPUtils.getString(context, Const.PREF_CHANGE_BACKGROUND);
        sCardBackgroundMode = SPUtils.getString(context, Const.PREF_CARD_BACKGROUND, "off");
        sSortMode = SPUtils.getString(context, Const.PREF_SORT_MODE);
        sIconPack = SPUtils.getString(context, Const.PREF_ICON_PACK);
        sCategory = SPUtils.getString(context, Const.PREF_CURR_CATEGORY, AnywhereType.DEFAULT_CATEGORY);
        sCurrentPage = SPUtils.getInt(context, Const.PREF_CURR_PAGE_NUM);
        sDumpInterval = SPUtils.getInt(context, Const.PREF_DUMP_INTERVAL, 1000);
    }

    public static void setsIsStreamCardMode(boolean sIsStreamCardMode) {
        GlobalValues.sIsStreamCardMode = sIsStreamCardMode;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.PREF_STREAM_CARD_MODE, sIsStreamCardMode);
    }

    public static void setsIsStreamCardModeSingleLine(boolean sIsStreamCardModeSingleLine) {
        GlobalValues.sIsStreamCardModeSingleLine = sIsStreamCardModeSingleLine;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.PREF_STREAM_CARD_SINGLE_LINE, sIsStreamCardModeSingleLine);
    }

    public static void setsCardBackgroundMode(String sCardBackgroundMode) {
        GlobalValues.sCardBackgroundMode = sCardBackgroundMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_CARD_BACKGROUND, sCardBackgroundMode);
    }

    public static void setsWorkingMode(String sWorkingMode) {
        GlobalValues.sWorkingMode = sWorkingMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_WORKING_MODE, sWorkingMode);
    }

    public static void setsActionBarType(String sActionBarType) {
        GlobalValues.sActionBarType = sActionBarType;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_ACTION_BAR_TYPE, sActionBarType);
    }

    public static void setsDarkMode(String sDarkMode) {
        GlobalValues.sDarkMode = sDarkMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_DARK_MODE, sDarkMode);
    }

    public static void setsBackgroundUri(String sBackgroundUri) {
        GlobalValues.sBackgroundUri = sBackgroundUri;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_CHANGE_BACKGROUND, sBackgroundUri);
    }

    public static void setsSortMode(String sSortMode) {
        GlobalValues.sSortMode = sSortMode;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_SORT_MODE, sSortMode);
    }

    public static void setsIconPack(String sIconPack) {
        GlobalValues.sIconPack = sIconPack;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_ICON_PACK, sIconPack);
    }

    public static void setsIsMd2Toolbar(boolean sIsMd2Toolbar) {
        GlobalValues.sIsMd2Toolbar = sIsMd2Toolbar;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.PREF_MD2_TOOLBAR, false);
    }

    public static void setsCategory(String sCategory, int page) {
        GlobalValues.sCategory = sCategory;
        GlobalValues.sCurrentPage = page;
        SPUtils.putString(AnywhereApplication.sContext, Const.PREF_CURR_CATEGORY, sCategory);
        SPUtils.putInt(AnywhereApplication.sContext, Const.PREF_CURR_PAGE_NUM, page);
    }

    public static void setsIsPages(boolean sIsPages) {
        GlobalValues.sIsPages = sIsPages;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.PREF_PAGES, sIsPages);
    }

    public static void setsIsCollectorPlus(boolean sIsCollectorPlus) {
        GlobalValues.sIsCollectorPlus = sIsCollectorPlus;
        SPUtils.putBoolean(AnywhereApplication.sContext, Const.PREF_COLLECTOR_PLUS, sIsCollectorPlus);
    }

    public static void setsDumpInterval(int sDumpInterval) {
        GlobalValues.sDumpInterval = sDumpInterval;
        SPUtils.putInt(AnywhereApplication.sContext, Const.PREF_DUMP_INTERVAL, sDumpInterval);
    }

    public static String getCollectorMode() {
        if (sIsCollectorPlus) {
            return "Collector+";
        } else {
            return "Collector";
        }
    }
}
