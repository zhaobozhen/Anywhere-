package com.absinthe.anywhere_.model;

public class Const {
    public static final String SP_NAME = "com.absinthe.anywhere__preferences";
    public static final String SP_NAME_DEBUG = "com.absinthe.anywhere_.debug_preferences";

    public static final String PREF_FIRST_LAUNCH = "isFirstLaunch";
    public static final String PREF_WORKING_MODE = "workingMode";
    public static final String PREF_DARK_MODE = "darkMode";
    public static final String PREF_CHANGE_BACKGROUND = "changeBackground";
    public static final String PREF_RESET_BACKGROUND = "resetBackground";
    public static final String PREF_DARK_MODE_OLED = "darkModeOLED";
    public static final String PREF_STREAM_CARD_MODE = "streamCardMode";
    public static final String PREF_STREAM_CARD_SINGLE_LINE = "streamCardModeSingleLine";
    public static final String PREF_CARD_BACKGROUND = "cardBackground";
    public static final String PREF_ACTION_BAR_TYPE = "actionBarType";
    public static final String PREF_HELP = "help";
    public static final String PREF_CLEAR_SHORTCUTS = "clearShortcuts";
    public static final String PREF_ICON_PACK = "iconPack";
    public static final String PREF_TILES = "tiles";
    public static final String PREF_SORT_MODE = "sortMode";
    public static final String PREF_BACKUP = "backup";
    public static final String PREF_BACKUP_SHARE = "backupShare";
    public static final String PREF_RESTORE = "restore";
    public static final String PREF_RESTORE_APPLY = "restoreApply";
    public static final String PREF_MD2_TOOLBAR = "md2Toolbar";
    public static final String PREF_PAGES = "pages";
    public static final String PREF_TILE_ONE = "tileOne";
    public static final String PREF_TILE_TWO = "tileTwo";
    public static final String PREF_TILE_THREE = "tileThree";
    public static final String PREF_TILE_ONE_LABEL = "tileOneLabel";
    public static final String PREF_TILE_TWO_LABEL = "tileTwoLabel";
    public static final String PREF_TILE_THREE_LABEL = "tileThreeLabel";
    public static final String PREF_TILE_ONE_CMD = "tileOneCmd";
    public static final String PREF_TILE_TWO_CMD = "tileTwoCmd";
    public static final String PREF_TILE_THREE_CMD = "tileThreeCmd";
    public static final String PREF_CURR_CATEGORY = "currCategory";
    public static final String PREF_CURR_PAGE_NUM = "currPageNum";

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
    public static final String CMD_OPEN_URL_SCHEME = "am start -a android.intent.action.VIEW -d ";
    public static final String CMD_OPEN_ACTIVITY = "am start -n ";
    public static final String CMD_OPEN_URL_SCHEME_FORMAT = "am start -a android.intent.action.VIEW -d %s";
    public static final String CMD_OPEN_ACTIVITY_FORMAT = "am start -n %s/%s";

    public static final int REQUEST_CODE_SHIZUKU_PERMISSION = 1001;
    public static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 1002;
    public static final int REQUEST_CODE_PHOTO_CROP = 1003;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 1004;
    public static final int REQUEST_CODE_WRITE_FILE = 1005;
    public static final int REQUEST_CODE_RESTORE_BACKUPS = 1006;

    public static final String HOST_URL = "url";
    public static final String HOST_OPEN = "open";

}
