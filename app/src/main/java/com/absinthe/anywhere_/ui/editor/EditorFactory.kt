package com.absinthe.anywhere_.ui.editor

import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.ui.editor.impl.*

object EditorFactory {

    @Throws(IllegalArgumentException::class)
    fun produce(type: Int): IEditor {
        return when (type) {
            AnywhereType.Card.URL_SCHEME -> SchemeEditorFragment()
            AnywhereType.Card.ACTIVITY -> AnywhereEditorFragment()
            AnywhereType.Card.QR_CODE -> QRCodeEditorFragment()
            AnywhereType.Card.IMAGE -> ImageEditorFragment()
            AnywhereType.Card.SHELL -> ShellEditorFragment()
            AnywhereType.Card.SWITCH_SHELL -> SwitchShellEditorFragment()
            AnywhereType.Card.FILE -> FileEditorFragment()
            AnywhereType.Card.BROADCAST -> BroadcastEditorFragment()
            else -> throw IllegalArgumentException("Editor type not exists.")
        }
    }

}