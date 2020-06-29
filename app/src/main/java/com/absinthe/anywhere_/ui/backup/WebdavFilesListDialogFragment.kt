package com.absinthe.anywhere_.ui.backup

import android.app.Dialog
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.backup.WebdavRestoreAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.WebdavFilesListBuilder
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebdavFilesListDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: WebdavFilesListBuilder
    private val adapter = WebdavRestoreAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = WebdavFilesListBuilder(requireContext())
        initView()

        return AnywhereDialogBuilder(requireContext()).setView(mBuilder.root)
                .setTitle(R.string.dialog_webdav_restore_title)
                .create()
    }

    private fun initView() {
        adapter.apply {
            setOnItemClickListener { _, _, position ->

            }
        }

        mBuilder.rvIconPack.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = this@WebdavFilesListDialogFragment.adapter
        }

        initData()
    }

    private fun initData() = lifecycleScope.launch(Dispatchers.IO) {
        val sardine = OkHttpSardine()
        sardine.setCredentials(GlobalValues.webdavUsername, GlobalValues.webdavPassword)

        try {
            val hostDir = GlobalValues.webdavHost + URLManager.BACKUP_DIR

            if (!sardine.exists(hostDir)) {
                return@launch
            }

            val list = sardine.list(hostDir)
            withContext(Dispatchers.Main) {
                adapter.setList(list)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                ToastUtil.makeText("Failed getting files: $e")
            }
        }
    }
}