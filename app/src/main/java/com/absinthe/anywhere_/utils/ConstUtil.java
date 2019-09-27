package com.absinthe.anywhere_.utils;

public class ConstUtil {
    public static final String SP_NAME = "com.absinthe.anywhere__preferences";
    public static final String SP_KEY_FIRST_LAUNCH = "isFirstLaunch";
    public static final String SP_KEY_WORKING_MODE = "workingMode";
    public static final String SP_KEY_DARK_MODE = "darkMode";
    public static final String SP_KEY_CHANGE_BACKGROUND = "changeBackground";
    public static final String SP_KEY_RESET_BACKGROUND = "resetBackground";
    public static final String SP_KEY_DARK_MODE_OLED = "darkModeOLED";

    public static final int SHORT_CLASS_NAME_TYPE = 0;
    public static final int FULL_CLASS_NAME_TYPE = 1;

    public static final String WORKING_MODE_ROOT = "root";
    public static final String WORKING_MODE_SHIZUKU = "shizuku";

    public static final String BUNDLE_PACKAGE_NAME = "packageName";
    public static final String BUNDLE_CLASS_NAME = "className";
    public static final String BUNDLE_CLASS_NAME_TYPE = "classNameType";

    public static final String INTENT_EXTRA_PACKAGE_NAME = "packageName";
    public static final String INTENT_EXTRA_CLASS_NAME = "className";
    public static final String INTENT_EXTRA_CLASS_NAME_TYPE = "classNameType";

    public static final String CMD_GET_TOP_STACK_ACTIVITY = "dumpsys activity activities | grep mResumedActivity";
}
