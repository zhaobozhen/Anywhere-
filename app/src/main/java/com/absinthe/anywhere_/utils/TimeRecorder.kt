package com.absinthe.anywhere_.utils

object TimeRecorder {
    private var start: Long = 0
    private var end: Long = 0

    var consumeTime = 0L
        get() = end - start
        private set

    var shouldRecord = true

    fun startRecord() {
        start = System.currentTimeMillis()
    }

    fun endRecord(): Long {
        end = System.currentTimeMillis()
        return end - start
    }

}