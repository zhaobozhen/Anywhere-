package com.absinthe.anywhere_.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.adapter.background.BackgroundAdapter
import com.absinthe.anywhere_.databinding.ActivityBackgroundBinding
import com.absinthe.anywhere_.interfaces.OnDocumentResultListener
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.utils.manager.IzukoHelper.isHitagi
import com.chad.library.adapter.base.BaseQuickAdapter

class BackgroundActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBackgroundBinding
    private var mAdapter: BackgroundAdapter = BackgroundAdapter()

    override fun setViewBinding() {
        isPaddingToolbar = true
        mBinding = ActivityBackgroundBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isHitagi) {
            finish()
        }

        mBinding.rvList.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE)
            setDocumentResultListener(object : OnDocumentResultListener {
                override fun onResult(uri: Uri) {
                    mAdapter.getItem(position)?.let {
                        it.backgroundUri = uri.toString()
                        mAdapter.setData(position, it)
                        AnywhereApplication.sRepository.updatePage(it)
                    }
                }
            })
        }
        mBinding.rvList.adapter = mAdapter

        AnywhereApplication.sRepository.allPageEntities.value?.let {
            mAdapter.addData(it)

        }
    }
}