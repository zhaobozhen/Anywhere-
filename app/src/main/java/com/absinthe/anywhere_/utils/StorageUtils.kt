package com.absinthe.anywhere_.utils

import android.content.Context
import android.os.Environment
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.AnywhereType
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

  suspend fun restoreFromJson(context: Context, jsonString: String) {
    val content = CipherUtils.decrypt(jsonString)
    Timber.d(content)

    try {
      val backupBean = Gson().fromJson(content, BackupBean::class.java)
      if (backupBean == null) {
        withContext(Dispatchers.Main) {
          ToastUtil.makeText(R.string.toast_backup_file_error)
        }
      } else {
        val aeList = mutableListOf<AnywhereEntity>()
        val pageList = mutableListOf<PageEntity>()
        pageList.addAll(backupBean.pageList)

        for (ae in backupBean.anywhereList) {
          if (!pageList.any { it.title == ae.category }) {
            val category = ae.category.orEmpty().ifEmpty { AnywhereType.Category.DEFAULT_CATEGORY }
            pageList.add(
              PageEntity().apply {
                title = category
                priority = AnywhereApplication.sRepository.allPageEntities.value?.size ?: 0
              }
            )
            ae.category = category
          }
          aeList.add(ae)
        }
        AnywhereApplication.sRepository.insert(aeList)
        AnywhereApplication.sRepository.insertPage(pageList)

        withContext(Dispatchers.Main) {
          ToastUtil.makeText(context.getString(R.string.toast_restore_success))
        }
      }
    } catch (e: Exception) {
      Timber.e(e)

      try {
        val entity = Gson().fromJson(content, AnywhereEntity::class.java)
        entity.category = GlobalValues.category

        AnywhereApplication.sRepository.insert(entity)

        withContext(Dispatchers.Main) {
          ToastUtil.makeText(context.getString(R.string.toast_restore_success))
        }
      } catch (e: Exception) {
        Timber.e(e)
        withContext(Dispatchers.Main) {
          ToastUtil.makeText(R.string.toast_backup_file_error)
        }
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

        val backupName =
          "Anywhere-Backups-${AppTextUtils.webDavFormatDate}-${BuildConfig.VERSION_NAME}.awbackups"

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
