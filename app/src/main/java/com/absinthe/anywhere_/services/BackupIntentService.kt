package com.absinthe.anywhere_.services

import android.app.IntentService
import android.content.Intent
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.utils.*
import com.absinthe.anywhere_.utils.manager.URLManager
import com.blankj.utilcode.util.NotificationUtils
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine

class BackupIntentService : IntentService("BackupIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        if (GlobalValues.webdavHost.isEmpty() ||
                GlobalValues.webdavUsername.isEmpty() ||
                GlobalValues.webdavPassword.isEmpty()) {
            return
        }
        NotifyUtils.createBackupNotification(this)

        val sardine = OkHttpSardine()
        sardine.setCredentials(GlobalValues.webdavUsername, GlobalValues.webdavPassword)

        try {
            val hostDir = GlobalValues.webdavHost + URLManager.BACKUP_DIR

            if (!sardine.exists(hostDir)) {
                sardine.createDirectory(hostDir)
            }

            val backupName = "Anywhere-Backups-${AppTextUtils.webDavFormatDate}-${BuildConfig.VERSION_NAME}.awbackups"

            StorageUtils.exportAnywhereEntityJsonString()?.let { content ->
                CipherUtils.encrypt(content)?.let { encrypted ->
                    if (!sardine.exists(hostDir + backupName)) {
                        sardine.put(hostDir + backupName, encrypted.toByteArray())

                        val list = sardine.list(hostDir)
                        if (list.size > 25) {
                            list.sortByDescending { it.creation }
                            val oldList = list.subList(25, list.size - 1)
                            for (res in oldList) {
                                sardine.delete(res.path)
                            }
                        }

                        NotificationUtils.cancel(NotifyUtils.BACKUP_NOTIFICATION_ID)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.makeText("Backup failed: $e")
            NotificationUtils.cancel(NotifyUtils.BACKUP_NOTIFICATION_ID)
        }
    }
}
