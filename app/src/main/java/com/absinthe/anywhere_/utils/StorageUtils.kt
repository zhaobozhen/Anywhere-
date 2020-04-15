package com.absinthe.anywhere_.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

object StorageUtils {
    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    /* Checks if external storage is available to at least read */
    val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    /**
     * Create a file
     *
     * @param activity context
     * @param mimeType MIME type of the file
     * @param fileName file name
     */
    @JvmStatic
    fun createFile(activity: AppCompatActivity, mimeType: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type.
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        try {
            activity.startActivityForResult(intent, Const.REQUEST_CODE_WRITE_FILE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            ToastUtil.makeText(R.string.toast_no_document_app)
        }
    }

    /**
     * Export Anywhere- entities to json string
     *
     * @return json string
     */
    @JvmStatic
    fun exportAnywhereEntityJsonString(): String? {
        val gson = Gson()
        AnywhereApplication.sRepository.allAnywhereEntities?.value?.let {
            for (ae in it) {
                ae.type = ae.anywhereType + ae.exportedType * 100
            }
            val s = gson.toJson(it)
            Timber.d(s)
            return s
        }

        return null
    }

    @Throws(IOException::class)
    fun storageToken(context: Context, token: String) {
        val fileName = "Token"
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            return
        }

        context.openFileOutput(fileName, Context.MODE_PRIVATE).apply {
            write(token.toByteArray())
            close()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getTokenFromFile(context: Context): String {
        val fileName = "Token"
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return ""
        }

        val fis = context.openFileInput(fileName)
        if (fis.available() == 0) {
            return ""
        }

        val buffer = ByteArray(fis.available())
        while (fis.read(buffer) != -1) { }
        fis.close()
        return String(buffer)
    }
}