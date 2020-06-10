package com.absinthe.anywhere_.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.BackupBean
import com.absinthe.anywhere_.ui.backup.BackupActivity
import com.absinthe.anywhere_.utils.manager.URLManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    fun exportAnywhereEntityJsonString(): String? {
        val anywhereList = AnywhereApplication.sRepository.allAnywhereEntities.value
        val pageList = AnywhereApplication.sRepository.allPageEntities.value

        return if (anywhereList == null || pageList == null) {
            null
        } else {
            for (ae in anywhereList) {
                ae.type = ae.anywhereType + ae.exportedType * 100
                ae.iconUri = ""
            }
            val backupBean = BackupBean(anywhereList, pageList)
            Gson().toJson(backupBean)
        }
    }

    fun restoreFromJson(context: Context, jsonString: String) {
        val content = CipherUtils.decrypt(jsonString)
        Timber.d(content)

        try {
            val backupBean = Gson().fromJson(content, BackupBean::class.java)
            if (backupBean == null) {
                ToastUtil.makeText(R.string.toast_backup_file_error)
            } else {
                BackupActivity.INSERT_CORRECT = true

                for (ae in backupBean.anywhereList) {
                    if (!BackupActivity.INSERT_CORRECT) {
                        ToastUtil.makeText(R.string.toast_backup_file_error)
                        break
                    }
                    AnywhereApplication.sRepository.insert(ae)
                }

                for (pe in backupBean.pageList) {
                    if (!BackupActivity.INSERT_CORRECT) {
                        ToastUtil.makeText(R.string.toast_backup_file_error)
                        break
                    }
                    AnywhereApplication.sRepository.insertPage(pe)
                }

                if (BackupActivity.INSERT_CORRECT) {
                    ToastUtil.makeText(context.getString(R.string.toast_restore_success))
                }
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            ToastUtil.makeText(R.string.toast_backup_file_error)
        }
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
        while (fis.read(buffer) != -1) {
        }
        fis.close()
        return String(buffer)
    }

    suspend fun webdavBackup() {
        if (GlobalValues.webdavHost.isEmpty() ||
                GlobalValues.webdavUsername.isEmpty() ||
                GlobalValues.webdavPassword.isEmpty()) {
            return
        }

        withContext(Dispatchers.IO) {
            val sardine = OkHttpSardine()
            sardine.setCredentials(GlobalValues.webdavUsername, GlobalValues.webdavPassword)

            try {
                val hostDir = GlobalValues.webdavHost + URLManager.BACKUP_DIR

                if (!sardine.exists(hostDir)) {
                    sardine.createDirectory(hostDir)
                }

                val backupName = "Anywhere-Backups-${TextUtils.getWebDavFormatDate()}-${BuildConfig.VERSION_NAME}.awbackups"

                exportAnywhereEntityJsonString()?.let { content ->
                    CipherUtils.encrypt(content)?.let { encrypted ->
                        if (!sardine.exists(hostDir + backupName)) {
                            sardine.put(hostDir + backupName, encrypted.toByteArray())

                            withContext(Dispatchers.Main) {
                                ToastUtil.makeText(R.string.toast_backup_success)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    ToastUtil.makeText("Backup failed: $e")
                }
            }
        }
    }
}