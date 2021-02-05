package com.absinthe.anywhere_.ui.qrcode

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.SpacesItemDecoration
import com.absinthe.anywhere_.adapter.card.BaseCardAdapter
import com.absinthe.anywhere_.adapter.card.LAYOUT_MODE_MEDIUM
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding
import com.absinthe.anywhere_.databinding.CardQrCollectionTipBinding
import com.absinthe.anywhere_.model.manager.QRCollection
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.absinthe.libraries.utils.utils.UiUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QRCodeCollectionActivity : BaseActivity() {

    private lateinit var binding: ActivityQrcodeCollectionBinding
    private var mAdapter = BaseCardAdapter(LAYOUT_MODE_MEDIUM)

    override fun setViewBinding() {
        isPaddingToolbar = true
        binding = ActivityQrcodeCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.toolbar.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            sharedElementsUseOverlay = false
        }
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        super.onCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager(newConfig)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun initView() {
        super.initView()

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.QR_COLLECTION_TIP)) {
            val tipBinding = CardQrCollectionTipBinding.inflate(
                    layoutInflater, binding.llContainer, false)

            binding.llContainer.addView(tipBinding.root, 0)

            tipBinding.btnOk.setOnClickListener {
                binding.llContainer.removeView(tipBinding.root)
                Once.markDone(OnceTag.QR_COLLECTION_TIP)
            }
        }
        binding.apply {
            recyclerView.apply {
                adapter = mAdapter
                setRecyclerViewLayoutManager(resources.configuration)
                addItemDecoration(SpacesItemDecoration(resources.getDimension(R.dimen.cardview_item_margin).toInt()))
                addPaddingBottom(UiUtils.getNavBarHeight(windowManager))
            }
            srlQrCollection.isRefreshing = true
        }

        mAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int -> mAdapter.clickItem(view, position) }
        mAdapter.setOnItemLongClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int -> mAdapter.longClickItem(view, position, isEditMode = false) }

        lifecycleScope.launch(Dispatchers.Main) {
            mAdapter.setList(QRCollection.list)

            binding.srlQrCollection.apply {
                isRefreshing = false
                isEnabled = false
            }
        }
    }

    private fun setRecyclerViewLayoutManager(configuration: Configuration) {
        binding.recyclerView.layoutManager = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            WrapContentStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        } else {
            WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}