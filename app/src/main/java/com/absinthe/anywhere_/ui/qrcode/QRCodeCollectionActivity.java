package com.absinthe.anywhere_.ui.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.QRCollectionAdapter;
import com.absinthe.anywhere_.adapter.WrapContentStaggeredGridLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding;
import com.absinthe.anywhere_.model.OnceTag;
import com.absinthe.anywhere_.model.QRCollection;

import jonathanfinerty.once.Once;

public class QRCodeCollectionActivity extends BaseActivity {
    ActivityQrcodeCollectionBinding binding;
    QRCollectionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_collection);

        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.QR_COLLECTION_TIP)) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(
                    R.layout.card_qr_collection_tip, binding.llContainer, false);
            binding.llContainer.addView(viewGroup, 0);
            viewGroup.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                binding.llContainer.removeView(viewGroup);
                Once.markDone(OnceTag.QR_COLLECTION_TIP);
            });
        }

        mAdapter = new QRCollectionAdapter(this);
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new WrapContentStaggeredGridLayoutManager(2, RecyclerView.VERTICAL));

        QRCollection collection = QRCollection.Singleton.INSTANCE.getInstance();
        mAdapter.setItems(collection.getList());
    }
}
