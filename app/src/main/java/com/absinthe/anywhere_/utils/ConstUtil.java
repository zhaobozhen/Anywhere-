package com.absinthe.anywhere_.utils;

public class ConstUtil {
    public static final String SP_NAME = "AnywhereSharedPreference";
    public static final String SP_KEY_FIRST_LAUNCH = "isFirstLaunch";

    public static final int SHORT_CLASS_NAME_TYPE = 0;
    public static final int FULL_CLASS_NAME_TYPE = 1;

    public static final String BUNDLE_FIRST_LAUNCH = "isFirstLaunch";
    public static final String BUNDLE_PACKAGE_NAME = "packageName";
    public static final String BUNDLE_CLASS_NAME = "className";
    public static final String BUNDLE_CLASS_NAME_TYPE = "classNameType";

    public static final String CMD_GET_TOP_STACK_ACTIVITY = "dumpsys activity activities | grep mResumedActivity";
}
