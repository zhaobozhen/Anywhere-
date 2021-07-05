package com.absinthe.anywhere_.ui.qrcode

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.SpacesItemDecoration
import com.absinthe.anywhere_.adapter.card.BaseCardAdapter
import com.absinthe.anywhere_.adapter.card.LAYOUT_MODE_MEDIUM
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding
import com.absinthe.anywhere_.databinding.CardQrCollectionTipBinding
import com.absinthe.anywhere_.model.manager.QRCollection
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rikka.widget.borderview.BorderView

class QRCodeCollectionActivity : AppBarActivity<ActivityQrcodeCollectionBinding>() {

    private val mAdapter by lazy { BaseCardAdapter(LAYOUT_MODE_MEDIUM) }

    override fun setViewBinding() = ActivityQrcodeCollectionBinding.inflate(layoutInflater)

    override fun getToolBar() = binding.toolbar.toolBar

    override fun getAppBarLayout() = binding.toolbar.appBar

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
            val tipBinding = CardQrCollectionTipBinding.inflate(layoutInflater)

            mAdapter.addHeaderView(tipBinding.root)

            tipBinding.btnOk.setOnClickListener {
                mAdapter.removeHeaderView(tipBinding.root)
                Once.markDone(OnceTag.QR_COLLECTION_TIP)
            }
        }
        binding.apply {
            list.apply {
                adapter = mAdapter
                setRecyclerViewLayoutManager(resources.configuration)
                addItemDecoration(SpacesItemDecoration(resources.getDimension(R.dimen.cardview_item_margin).toInt()))
                borderVisibilityChangedListener =
                    BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                        appBar?.setRaised(!top)
                    }
            }
            progressHorizontal.show()
        }

        mAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int -> mAdapter.clickItem(view, position) }
        mAdapter.setOnItemLongClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int -> mAdapter.longClickItem(view, position, isEditMode = false) }

        lifecycleScope.launch(Dispatchers.Main) {
            mAdapter.setList(QRCollection.list)
            binding.progressHorizontal.hide()
        }
    }

    private fun setRecyclerViewLayoutManager(configuration: Configuration) {
        binding.list.layoutManager = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            WrapContentStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        } else {
            WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}