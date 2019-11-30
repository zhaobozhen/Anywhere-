package com.absinthe.anywhere_.ui.qrcode;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.QRCollectionAdapter;
import com.absinthe.anywhere_.adapter.WrapContentStaggeredGridLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityQrcodeCollectionBinding;
import com.absinthe.anywhere_.model.QRCollection;

public class QRCodeCollectionActivity extends BaseActivity {
    ActivityQrcodeCollectionBinding binding;
    QRCollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_collection);

        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        adapter = new QRCollectionAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new WrapContentStaggeredGridLayoutManager(2, RecyclerView.VERTICAL));

        QRCollection collection = QRCollection.Singleton.INSTANCE.getInstance();
        adapter.setItems(collection.getList());
        adapter.notifyDataSetChanged();
    }
}
