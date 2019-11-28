package com.absinthe.anywhere_.model;

public class Const {
    public static final String SP_NAME = "com.absinthe.anywhere__preferences";
    public static final String SP_NAME_DEBUG = "com.absinthe.anywhere_.debug_preferences";

    public static final String SP_KEY_FIRST_LAUNCH = "isFirstLaunch";
    public static final String SP_KEY_WORKING_MODE = "workingMode";
    public static final String SP_KEY_DARK_MODE = "darkMode";
    public static final String SP_KEY_CHANGE_BACKGROUND = "changeBackground";
    public static final String SP_KEY_RESET_BACKGROUND = "resetBackground";
    public static final String SP_KEY_DARK_MODE_OLED = "darkModeOLED";
    public static final String SP_KEY_STREAM_CARD_MODE = "streamCardMode";
    public static final String SP_KEY_STREAM_CARD_SINGLE_LINE = "streamCardModeSingleLine";
    public static final String SP_KEY_CARD_BACKGROUND = "cardBackground";
    public static final String SP_KEY_ACTION_BAR_TITLE = "actionBarTitle";
    public static final String SP_KEY_ACTION_BAR_TYPE = "actionBarType";
    public static final String SP_KEY_HELP = "help";
    public static final String SP_KEY_CLEAR_SHORTCUTS = "clearShortcuts";
    public static final String SP_KEY_ICON_PACK = "iconPack";
    public static final String SP_KEY_SORT_MODE = "sortMode";
    public static final String SP_KEY_BACKUP = "backup";
    public static final String SP_KEY_RESTORE = "restore";

    public static final String WORKING_MODE_URL_SCHEME = "url_scheme";
    public static final String WORKING_MODE_ROOT = "root";
    public static final String WORKING_MODE_SHIZUKU = "shizuku";

    public static final String ACTION_BAR_TYPE_LIGHT = "light";
    public static final String ACTION_BAR_TYPE_DARK = "dark";

    public static final String SORT_MODE_TIME_ASC = "TIME_ASC";
    public static final String SORT_MODE_TIME_DESC = "TIME_DESC";
    public static final String SORT_MODE_NAME_ASC = "NAME_ASC";
    public static final String SORT_MODE_NAME_DESC = "NAME_DESC";

    public static final String INTENT_EXTRA_PARAM_1 = "param1";
    public static final String INTENT_EXTRA_PARAM_2 = "param2";
    public static final String INTENT_EXTRA_PARAM_3 = "param3";

    public static final String INTENT_EXTRA_SHORTCUTS_CMD = "shortcutsCmd";
    public static final String INTENT_EXTRA_WIDGET_ENTITY = "entity";
    public static final String INTENT_EXTRA_WIDGET_COMMAND = "command";
    public static final String INTENT_EXTRA_APP_NAME = "appName";
    public static final String INTENT_EXTRA_PKG_NAME = "pkgName";

    public static final String CMD_GET_TOP_STACK_ACTIVITY = "dumpsys activity activities | grep mResumedActivity";
    public static final String CMD_OPEN_URL_SCHEME = "am start -a android.intent.action.VIEW -d %s";
    public static final String CMD_OPEN_ACTIVITY = "am start -n %s/%s";

    public static final int REQUEST_CODE_SHIZUKU_PERMISSION = 1001;
    public static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 1002;
    public static final int REQUEST_CODE_PHOTO_CROP = 1003;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 1004;
    public static final int REQUEST_CODE_WRITE_FILE = 1005;
    public static final int REQUEST_CODE_RESTORE_BACKUPS = 1006;

}
