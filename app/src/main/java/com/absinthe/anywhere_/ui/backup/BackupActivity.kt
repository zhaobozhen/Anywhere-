package com.absinthe.anywhere_.ui.backup

import android.app.Activity
import android.content.Intent
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.databinding.ActivityBackupBinding
import com.absinthe.anywhere_.utils.CipherUtils.encrypt
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.utils.StorageUtils.exportAnywhereEntityJsonString
import com.absinthe.anywhere_.utils.ToastUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class BackupActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBackupBinding

    override fun setViewBinding() {
        isPaddingToolbar = true
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
                    ToastUtil.makeText(getString(R.string.toast_runtime_error))
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

                        StorageUtils.restoreFromJson(this, stringBuilder.toString())

                        inputStream.close()
                        reader.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}