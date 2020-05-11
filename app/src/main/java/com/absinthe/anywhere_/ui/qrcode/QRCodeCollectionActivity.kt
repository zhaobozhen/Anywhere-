package com.absinthe.anywhere_.ui.qrcode

import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.card.BaseCardAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding
import com.absinthe.anywhere_.databinding.CardQrCollectionTipBinding
import com.absinthe.anywhere_.model.QRCollection
import com.absinthe.anywhere_.utils.StatusBarUtil
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QRCodeCollectionActivity : BaseActivity() {

    private lateinit var binding: ActivityQrcodeCollectionBinding
    private var mAdapter = BaseCardAdapter(R.layout.item_stream_card_view)

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        binding = ActivityQrcodeCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.toolbar.toolbar
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
                layoutManager = WrapContentStaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + StatusBarUtil.getNavBarHeight())
            }
            srlQrCollection.isRefreshing = true
        }

        GlobalScope.launch(Dispatchers.Main) {
            val collection = QRCollection.Singleton.INSTANCE.instance
            mAdapter.setNewInstance(collection.list)

            binding.srlQrCollection.apply {
                isRefreshing = false
                isEnabled = false
            }
        }
    }
}