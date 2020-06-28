package com.absinthe.anywhere_.model

import android.content.Context
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception

object SuProcess {

    fun exec(cmd: String): String {
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            val inputStream = BufferedReader(InputStreamReader(process.inputStream))
            val errorStream = BufferedReader(InputStreamReader(process.errorStream))

            outputStream.apply {
                writeBytes("$cmd\n")
                flush()
                writeBytes("exit\n")
                flush()
                close()
            }

            val sb = StringBuilder()
            var line: String

            while (errorStream.readLine().also { line = it } != null && line != "null") {
                sb.append(line).appendln()
            }
            while (inputStream.readLine().also { line = it } != null && line != "null") {
                sb.append(line).appendln()
            }

            process.waitFor()
            inputStream.close()
            return sb.toString()
        } catch (e: IOException) {
            return e.toString()
        }
    }

    fun acquireRootPerm(context: Context): Boolean {
        var process: Process? = null
        var outputStream: DataOutputStream? = null
        var inputStream: BufferedReader? = null

        try {
            process = Runtime.getRuntime().exec("su")
            outputStream = DataOutputStream(process.outputStream)
            inputStream = BufferedReader(InputStreamReader(process.inputStream))

            outputStream.apply {
                writeBytes("chmod 777 ${context.packageCodePath}\n")
                flush()
                writeBytes("exit\n")
                flush()
                process.waitFor()
            }

        } catch (e: Exception) {
            return false
        } finally {
            outputStream?.close()
            inputStream?.close()
            process?.destroy()
        }
        return true
    }
}