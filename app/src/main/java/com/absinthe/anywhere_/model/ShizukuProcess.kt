package com.absinthe.anywhere_.model

import moe.shizuku.api.ShizukuService
import java.lang.StringBuilder

object ShizukuProcess {
    private val process = ShizukuService.newProcess(arrayOf("sh"), null, null)
    private val outputStream = process.outputStream
    private val inputStream = process.inputStream

    fun exec(cmd: String): String {
        val sb = StringBuilder()
        outputStream?.apply {
            write("$cmd\n".toByteArray())
            write("exit\n".toByteArray())
            close()
        }
        var c: Int
        while (inputStream.read().also { c = it } != -1) {
            sb.append(c.toChar())
        }
        inputStream.close()
        return sb.toString()
    }
}