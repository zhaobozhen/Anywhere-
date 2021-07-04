package com.absinthe.anywhere_.constants

object Const {

    const val SP_NAME = "com.absinthe.anywhere__preferences"
    const val SP_NAME_DEBUG = "com.absinthe.anywhere_.debug_preferences"
    const val SHORTCUTS_LIST = "shortcutsList"
    const val PREF_WORKING_MODE = "workingMode"
    const val PREF_DARK_MODE = "darkMode"
    const val PREF_CHANGE_BACKGROUND = "changeBackground"
    const val PREF_RESET_BACKGROUND = "resetBackground"
    const val PREF_CLOSE_AFTER_LAUNCH = "closeAfterLaunch"

    const val PREF_CARD_MODE = "cardMode"
    const val PREF_CARD_MODE_LARGE = "large"
    const val PREF_CARD_MODE_MEDIUM = "medium"
    const val PREF_CARD_MODE_SMALL = "small"
    const val PREF_CARD_MODE_MINIMUM = "minimum"
    const val PREF_CARD_BACKGROUND = "cardBackgroundMode"
    const val PREF_CARD_LAYOUT = "cardLayout"

    const val PREF_ACTION_BAR_TYPE = "actionBarType"
    const val PREF_HELP = "help"
    const val PREF_BETA = "beta"
    const val PREF_CLEAR_SHORTCUTS = "clearShortcuts"
    const val PREF_ICON_PACK = "iconPack"
    const val PREF_TILES = "tiles"
    const val PREF_SORT_MODE = "sortMode"
    const val PREF_BACKUP = "backup"
    const val PREF_BACKUP_SHARE = "backupShare"
    const val PREF_RESTORE = "restore"
    const val PREF_RESTORE_APPLY = "restoreApply"
    const val PREF_MD2_TOOLBAR = "md2Toolbar"
    const val PREF_PAGES = "pages"
    const val PREF_CURR_CATEGORY = "currCategory"
    const val PREF_CURR_PAGE_NUM = "currPageNum"
    const val PREF_COLLECTOR_PLUS = "collectorPlus"
    const val PREF_EXCLUDE_FROM_RECENT = "excludeFromRecent"
    const val PREF_SHOW_SHELL_RESULT_MODE = "showShellResultMode"
    const val PREF_DUMP_INTERVAL = "dumpInterval"
    const val PREF_AUTO_DARK_MODE_START = "autoDarkModeStart"
    const val PREF_AUTO_DARK_MODE_END = "autoDarkModeEnd"
    const val PREF_DEFROST_MODE = "defrostMode"
    const val PREF_WEBDAV_HOST = "webdavHost"
    const val PREF_WEBDAV_USERNAME = "webdavUsername"
    const val PREF_WEBDAV_PASSWORD = "webdavPassword"
    const val PREF_WEBDAV_BACKUP = "webdavBackup"
    const val PREF_WEBDAV_AUTO_BACKUP = "webdavAutoBackup"
    const val PREF_WEBDAV_RESTORE = "webdavRestore"
    const val PREF_NEED_BACKUP = "needBackup"
    const val PREF_TRANS_ICON = "transparentIcon"
    const val PREF_DEPRECATED_SC_CREATING_METHOD = "deprecatedScCreatingMethod"
    const val PREF_EDITOR_ENTRY_ANIM = "editorEntryAnim"
    const val PREF_SHOW_SYSTEM_APPS = "showSystemApps"

    const val WORKING_MODE_URL_SCHEME = "url_scheme"
    const val WORKING_MODE_ROOT = "root"
    const val WORKING_MODE_SHIZUKU = "shizuku"

    const val ACTION_BAR_TYPE_LIGHT = "light"
    const val ACTION_BAR_TYPE_DARK = "dark"

    const val CARD_BG_MODE_OFF = "off"
    const val CARD_BG_MODE_PURE = "pure"
    const val CARD_BG_MODE_GRADIENT = "gradient"
    const val CARD_BG_MODE_BLURRY = "blurry"

    const val DARK_MODE_OFF = "off"
    const val DARK_MODE_ON = "on"
    const val DARK_MODE_AUTO = "auto"
    const val DARK_MODE_SYSTEM = "system"
    const val DARK_MODE_BATTERY = "battery"

    const val SORT_MODE_TIME_ASC = "TIME_ASC"
    const val SORT_MODE_TIME_DESC = "TIME_DESC"
    const val SORT_MODE_NAME_ASC = "NAME_ASC"
    const val SORT_MODE_NAME_DESC = "NAME_DESC"

    const val SHELL_RESULT_OFF = "off"
    const val SHELL_RESULT_TOAST = "toast"
    const val SHELL_RESULT_DIALOG = "dialog"

    const val INTENT_EXTRA_PARAM_1 = "param1"
    const val INTENT_EXTRA_PARAM_2 = "param2"
    const val INTENT_EXTRA_PARAM_3 = "param3"
    const val INTENT_EXTRA_TYPE = "type"
    const val INTENT_EXTRA_SHORTCUTS_CMD = "shortcutsCmd" // Old scheme
    const val INTENT_EXTRA_SHORTCUTS_ID = "shortcutsId"
    const val INTENT_EXTRA_OPEN_SHORT_ID = "sid"
    const val INTENT_EXTRA_DYNAMIC_PARAM = "dynamic"
    const val INTENT_EXTRA_FROM_TILE = "tile"
    const val INTENT_EXTRA_EMULATE_BACK_PRESS = "emulateBack"

    const val INTENT_EXTRA_WIDGET_ENTITY = "entity"

    const val INTENT_EXTRA_APP_NAME = "appName"
    const val INTENT_EXTRA_PKG_NAME = "pkgName"

    const val CMD_GET_TOP_STACK_ACTIVITY = "dumpsys activity activities | grep mResumedActivity"
    const val CMD_OPEN_URL_SCHEME = "am start -a android.intent.action.VIEW -d "
    const val CMD_OPEN_URL_SCHEME_FORMAT = "am start -a android.intent.action.VIEW -d %s"
    const val CMD_OPEN_ACTIVITY_FORMAT = "am start -n %s/%s"
    const val CMD_START_BROADCAST_FORMAT = "am broadcast"
    const val CMD_BACK_PRESS = "input keyevent 4"

    const val REQUEST_CODE_SHIZUKU_PERMISSION = 1001
    const val REQUEST_CODE_GO_TO_MIUI_PERM_MANAGER = 1002
    const val REQUEST_CODE_R_CONTROL = 1003
    const val REQUEST_CODE_IMAGE_CAPTURE = 1004
    const val REQUEST_CODE_WRITE_FILE = 1005
    const val REQUEST_CODE_RESTORE_BACKUPS = 1006
    const val REQUEST_CODE_ICEBOX = 1007
    const val REQUEST_CODE_DSM = 1008
    const val REQUEST_CODE_DPM = 1009
    const val REQUEST_CODE_OPEN_EDITOR = 1010
    const val REQUEST_CODE_APP_LIST_SELECT = 1011
    const val REQUEST_CODE_APP_DETAIL_SELECT = 1012

    const val DEFROST_MODE_ROOT = "root"
    const val DEFROST_MODE_ICEBOX_SDK = "icebox"
    const val DEFROST_MODE_DSM = "dsm"
    const val DEFROST_MODE_DPM = "dpm"
    const val DEFROST_MODE_SHIZUKU = "shizuku"

    const val DEFAULT_ICON_PACK = "default.icon.pack"
    const val DEFAULT_BR_ACTION = "com.absinthe.anywhere_.BROADCAST"
}