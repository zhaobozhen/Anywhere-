package com.absinthe.anywhere_.constants

import com.absinthe.anywhere_.R
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
object AnywhereType {

    object Card {
        const val URL_SCHEME = 0
        const val ACTIVITY = 1
        const val MINI_PROGRAM = 2
        const val QR_CODE = 3
        const val IMAGE = 4
        const val SHELL = 5
        const val SWITCH_SHELL = 6
        const val FILE = 7
        const val BROADCAST = 8
        const val WORKFLOW = 9

        val NEW_TITLE_MAP = hashMapOf(
                Pair(URL_SCHEME, "New URL Scheme"),
                Pair(ACTIVITY, "New Activity"),
                Pair(MINI_PROGRAM, "New Mini Program"),
                Pair(QR_CODE, "New QR Code"),
                Pair(IMAGE, "New Image"),
                Pair(SHELL, "New Shell"),
                Pair(SWITCH_SHELL, "New Switch Shell"),
                Pair(FILE, "New File"),
                Pair(BROADCAST, "New Broadcast"),
                Pair(WORKFLOW, "New Workflow")
        )

        val TYPE_STRINGRES_MAP = hashMapOf(
                Pair(URL_SCHEME, R.string.btn_url_scheme),
                Pair(ACTIVITY, R.string.btn_activity),
                Pair(MINI_PROGRAM, R.string.btn_add_mini_program),
                Pair(QR_CODE, R.string.btn_add_qr_code),
                Pair(IMAGE, R.string.btn_add_image),
                Pair(SHELL, R.string.btn_add_shell),
                Pair(SWITCH_SHELL, R.string.btn_add_switch_shell),
                Pair(FILE, R.string.btn_add_file),
                Pair(BROADCAST, R.string.btn_add_broadcast),
                Pair(WORKFLOW, R.string.btn_add_workflow)
        )
    }

    object Property {
        const val NONE = 0
        const val SHORTCUTS = 1
        const val EXPORTED = 1
    }

    object WhereMode {
        const val ANYWHERE = "Anywhere-"
        const val SOMEWHERE = "Somewhere-"
        const val NOWHERE = "Nowhere-"
    }

    object Category {
        const val DEFAULT_CATEGORY = "Default"
    }

    object Page {
        const val CARD_PAGE = 0
        const val WEB_PAGE = 1
    }

    object Prefix {
        const val IMAGE_PREFIX = "[Image]"
        const val QRCODE_PREFIX = "[QR_Code]"
        const val DYNAMIC_PARAMS_PREFIX = "[DYNAMIC_PARAMS "
        const val DYNAMIC_PARAMS_PREFIX_FORMAT = "[DYNAMIC_PARAMS %s]"
        const val SHELL_PREFIX = "[ANYWHERE_SHELL]"
    }
}