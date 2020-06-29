package com.absinthe.anywhere_.ui.backup

import android.app.Dialog
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.backup.WebdavRestoreAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.LayoutWebdavRestoreBinding
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.blankj.utilcode.util.ConvertUtils
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebdavFilesListDialogFragment : AnywhereDialogFragment() {

    private lateinit var binding: LayoutWebdavRestoreBinding
    private val adapter = WebdavRestoreAdapter()
    private val sardine = OkHttpSardine()
    private val hostDir = GlobalValues.webdavHost + URLManager.BACKUP_DIR

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutWebdavRestoreBinding.inflate(layoutInflater)
        initView()
        sardine.setCredentials(GlobalValues.webdavUsername, GlobalValues.webdavPassword)

        return AnywhereDialogBuilder(requireContext()).setView(binding.root)
                .setTitle(R.string.dialog_webdav_restore_title)
                .create()
    }

    private fun initView() {
        adapter.apply {
            setOnItemClickListener { _, _, position ->
                applyBackupFile(hostDir + data[position].displayName)
                dismiss()
            }
        }

        binding.rvList.adapter = adapter
        binding.root.displayedChild = 0
        initData()
    }

    private fun initData() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            if (!sardine.exists(hostDir)) {
                return@launch
            }

            val list = sardine.list(hostDir)
            withContext(Dispatchers.Main) {
                adapter.setList(list.filter { !it.isDirectory }.sortedByDescending { it.displayName })
                binding.root.displayedChild = 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                ToastUtil.makeText("Failed getting files: $e")
            }
        }
    }

    private fun applyBackupFile(url: String) = lifecycleScope.launch(Dispatchers.IO) {
        sardine.get(url)?.let {
            val result = ConvertUtils.inputStream2String(it, "UTF-8")
            result?.apply {
                withContext(Dispatchers.Main) {
                    StorageUtils.restoreFromJson(requireContext(), this@apply)
                }
            }
        }
    }
}