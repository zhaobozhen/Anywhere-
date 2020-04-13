package com.absinthe.anywhere_.ui.backup

import android.app.Activity
import android.content.Intent
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.databinding.ActivityBackupBinding
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.AnywhereType
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.PageEntity
import com.absinthe.anywhere_.utils.CipherUtils.decrypt
import com.absinthe.anywhere_.utils.CipherUtils.encrypt
import com.absinthe.anywhere_.utils.ListUtils
import com.absinthe.anywhere_.utils.StorageUtils.exportAnywhereEntityJsonString
import com.absinthe.anywhere_.utils.ToastUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class BackupActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBackupBinding

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        mBinding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Const.REQUEST_CODE_WRITE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                try {
                    contentResolver.openOutputStream(it)?.let { os ->
                        exportAnywhereEntityJsonString()?.let { content ->
                            encrypt(content)?.let { encrypted ->
                                os.write(encrypted.toByteArray())
                                os.close()
                                ToastUtil.makeText(getString(R.string.toast_backup_success))
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == Const.REQUEST_CODE_RESTORE_BACKUPS && resultCode == Activity.RESULT_OK) {
            data?.data?.let { intentData ->
                try {
                    contentResolver.openInputStream(intentData)?.let { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        val content = decrypt(stringBuilder.toString())
                        Timber.d(content)

                        Gson().fromJson<List<AnywhereEntity>>(content,
                                object : TypeToken<List<AnywhereEntity?>?>() {}.type)?.let { list ->
                            INSERT_CORRECT = true

                            for (ae in list) {
                                if (!INSERT_CORRECT) {
                                    ToastUtil.makeText(R.string.toast_backup_file_error)
                                    break
                                }
                                AnywhereApplication.sRepository.allPageEntities?.value?.let { entities ->
                                    if (ListUtils.getPageEntityByTitle(ae.category) == null) {
                                        val pe = PageEntity.Builder().apply {
                                            title = ae.category
                                            priority = entities.size + 1
                                            type = AnywhereType.CARD_PAGE
                                        }
                                        AnywhereApplication.sRepository.insertPage(pe)
                                    }
                                }
                                AnywhereApplication.sRepository.insert(ae)
                            }

                            if (INSERT_CORRECT) {
                                ToastUtil.makeText(getString(R.string.toast_restore_success))
                            }
                        }

                        inputStream.close()
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        var INSERT_CORRECT = true
    }
}