package com.absinthe.anywhere_.ui.qrcode;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.adapter.card.QRCollectionAdapter;
import com.absinthe.anywhere_.adapter.manager.WrapContentStaggeredGridLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding;
import com.absinthe.anywhere_.databinding.CardQrCollectionTipBinding;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.QRCollection;

import jonathanfinerty.once.Once;

public class QRCodeCollectionActivity extends BaseActivity {

    private ActivityQrcodeCollectionBinding binding;
    private QRCollectionAdapter mAdapter;

    @Override
    protected void setViewBinding() {
        binding = ActivityQrcodeCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = binding.toolbar.toolbar;
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

    @Override
    protected void initView() {
        super.initView();

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.QR_COLLECTION_TIP)) {
            CardQrCollectionTipBinding tipBinding = CardQrCollectionTipBinding.inflate(
                    getLayoutInflater(), binding.llContainer, false);

            binding.llContainer.addView(tipBinding.getRoot(), 0);
            tipBinding.btnOk.setOnClickListener(v -> {
                binding.llContainer.removeView(tipBinding.getRoot());
                Once.markDone(OnceTag.QR_COLLECTION_TIP);
            });
        }

        mAdapter = new QRCollectionAdapter(this);
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new WrapContentStaggeredGridLayoutManager(2, RecyclerView.VERTICAL));

        binding.srlQrCollection.setRefreshing(true);
        new Handler(Looper.getMainLooper()).post(() -> {
            QRCollection collection = QRCollection.Singleton.INSTANCE.getInstance();
            mAdapter.setItems(collection.getList());
            binding.srlQrCollection.setRefreshing(false);
            binding.srlQrCollection.setEnabled(false);
        });
    }
}
