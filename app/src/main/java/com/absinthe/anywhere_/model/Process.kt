package com.absinthe.anywhere_.model

import java.lang.StringBuilder

object Process {
    private val process = Runtime.getRuntime().exec("su")
    private val outputStream = process.outputStream
    private val inputStream = process.inputStream

    fun exec(cmd: String): String {
        val sb = StringBuilder()
        outputStream?.apply {
            write("$cmd\n".toByteArray())
            flush()
            write("exit\n".toByteArray())
            flush()
        }

        var c: Int
        while (inputStream.read().also { c = it } != -1) {
            sb.append(c.toChar())
        }

        process.waitFor()
        outputStream?.close()
        inputStream?.close()
        return sb.toString()
    }
}