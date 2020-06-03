package com.absinthe.anywhere_.model

import moe.shizuku.api.ShizukuService
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

object ShizukuProcess {

    fun exec(cmd: String): String {
        val process = ShizukuService.newProcess(arrayOf("sh"), null, null)
        val outputStream = DataOutputStream(process.outputStream)
        val inputStream = BufferedReader(InputStreamReader(process.inputStream))

        outputStream.apply {
            writeBytes("$cmd\n")
            writeBytes("exit\n")
            close()
        }

        val sb = StringBuilder()
        var line: String

        while (inputStream.readLine().also { line = it } != null && line != "null") {
            sb.append(line).appendln()
        }
        inputStream.close()

        return sb.toString()
    }
}