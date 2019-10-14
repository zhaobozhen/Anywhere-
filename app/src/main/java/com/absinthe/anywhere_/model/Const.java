package com.absinthe.anywhere_.model;

public class Const {
    public static final String SP_NAME = "com.absinthe.anywhere__preferences";
    public static final String SP_KEY_FIRST_LAUNCH = "isFirstLaunch";
    public static final String SP_KEY_WORKING_MODE = "workingMode";
    public static final String SP_KEY_DARK_MODE = "darkMode";
    public static final String SP_KEY_CHANGE_BACKGROUND = "changeBackground";
    public static final String SP_KEY_RESET_BACKGROUND = "resetBackground";
    public static final String SP_KEY_DARK_MODE_OLED = "darkModeOLED";
    public static final String SP_KEY_ACTION_BAR_TITLE = "actionBarTitle";
    public static final String SP_KEY_ACTION_BAR_TYPE = "actionBarType";
    public static final String SP_KEY_SHORTCUTS = "shortcuts";

    public static final int SHORT_CLASS_NAME_TYPE = 0;
    public static final int FULL_CLASS_NAME_TYPE = 1;

    public static final String WORKING_MODE_URL_SCHEME = "url_scheme";
    public static final String WORKING_MODE_ROOT = "root";
    public static final String WORKING_MODE_SHIZUKU = "shizuku";

    public static final String ACTION_BAR_TYPE_LIGHT = "light";
    public static final String ACTION_BAR_TYPE_DARK = "dark";

    public static final String INTENT_EXTRA_PARAM_1 = "param1";
    public static final String INTENT_EXTRA_PARAM_2 = "param2";
    public static final String INTENT_EXTRA_PARAM_3 = "param3";

    public static final String INTENT_EXTRA_SHORTCUTS_CMD = "shortcutsCmd";

    public static final String CMD_GET_TOP_STACK_ACTIVITY = "dumpsys activity activities | grep mResumedActivity";
    public static final String CMD_OPEN_URL_SCHEME = "am start -a android.intent.action.VIEW -d ";

    public static final int REQUEST_CODE_SHIZUKU_PERMISSION = 1001;
    public static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 1002;
    public static final int REQUEST_CODE_PHOTO_CROP = 1003;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 1004;

}
