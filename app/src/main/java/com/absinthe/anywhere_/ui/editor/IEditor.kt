package com.absinthe.anywhere_.ui.editor

interface IEditor {
    var execWithRoot: Boolean
    fun tryRunning()
    fun doneEdit(): Boolean
}
