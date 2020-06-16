package com.absinthe.anywhere_.constants

const val WEIGHT_CARD = 1
const val WEIGHT_SHORTCUTS = 10
const val WEIGHT_EXPORTED = 100

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

    class Builder(type: Int = 0) {

        var cardType = type % WEIGHT_SHORTCUTS
        var shortcutsType = type % WEIGHT_EXPORTED / WEIGHT_SHORTCUTS
        var exportedType = type / WEIGHT_EXPORTED

        fun cardType(cardType: Int): Builder {
            this.cardType = cardType
            return this
        }

        fun isShortcut(flag: Boolean): Builder {
            this.shortcutsType = if (flag) Property.SHORTCUTS else Property.NONE
            return this
        }

        fun isExported(flag: Boolean): Builder {
            this.exportedType = if (flag) Property.EXPORTED else Property.NONE
            return this
        }

        fun build(): Int {
            return cardType * WEIGHT_CARD +
                    shortcutsType * WEIGHT_SHORTCUTS +
                    exportedType * WEIGHT_EXPORTED
        }
    }
}