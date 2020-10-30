package com.absinthe.anywhere_.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.BackupBean
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.utils.manager.URLManager
import com.google.gson.Gson
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.*
import timber.log.Timber

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
        val finalList = mutableListOf<AnywhereEntity>()

        return if (anywhereList == null || pageList == null) {
            null
        } else {
            for (ae in anywhereList) {
                if (ae.type == AnywhereType.Card.IMAGE || ae.type == AnywhereType.Card.FILE) {
                    continue
                }
                ae.iconUri = ""
                finalList.add(ae)
            }
            val backupBean = BackupBean(finalList, pageList)
            Gson().toJson(backupBean)
        }
    }

    fun restoreFromJson(context: Context, jsonString: String) {
        val content = CipherUtils.decrypt(jsonString)

        try {
            val backupBean = Gson().fromJson(content, BackupBean::class.java)
            if (backupBean == null) {
                ToastUtil.makeText(R.string.toast_backup_file_error)
            } else {
                for (pe in backupBean.pageList) {
                    AnywhereApplication.sRepository.insertPage(pe)
                }

                for (ae in backupBean.anywhereList) {
                    if (AnywhereApplication.sRepository.getPageEntityByTitle(ae.category) == null) {
                        val category = ae.category.ifEmpty { AnywhereType.Category.DEFAULT_CATEGORY }
                        AnywhereApplication.sRepository.insertPage(
                                PageEntity.Builder().apply {
                                    title = category
                                    priority = AnywhereApplication.sRepository.allPageEntities.value?.size ?: 0
                                }
                        )
                        ae.category = category
                    }
                    AnywhereApplication.sRepository.insert(ae)
                }

                ToastUtil.makeText(context.getString(R.string.toast_restore_success))
            }
        } catch (e: Exception) {
            Timber.e(e)

            try {
                val entity = Gson().fromJson(content, AnywhereEntity::class.java)
                entity.category = GlobalValues.category

                AnywhereApplication.sRepository.insert(entity)
            } catch (e: Exception) {
                Timber.e(e)
                ToastUtil.makeText(R.string.toast_backup_file_error)
            }
        }
    }

    fun webdavBackup() {
        GlobalScope.launch(Dispatchers.IO) {
            val sardine = OkHttpSardine()
            sardine.setCredentials(GlobalValues.webdavUsername, GlobalValues.webdavPassword)

            try {
                val hostDir = GlobalValues.webdavHost + URLManager.BACKUP_DIR

                while (!sardine.exists(hostDir)) {
                    sardine.createDirectory(hostDir)
                    delay(300)
                }

                val backupName = "Anywhere-Backups-${AppTextUtils.webDavFormatDate}-${BuildConfig.VERSION_NAME}.awbackups"

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