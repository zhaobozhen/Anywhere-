package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.manager.SmoothScrollLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityGiftBinding;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.viewmodel.GiftViewModel;

public class GiftActivity extends BaseActivity {

    private static GiftActivity sInstance;
    private ActivityGiftBinding mBinding;
    private GiftViewModel mViewModel;

    public static GiftActivity getInstance() {
        return sInstance;
    }

    public ActivityGiftBinding getBinding() {
        return mBinding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gift);
        mViewModel = new ViewModelProvider(this).get(GiftViewModel.class);

        initView();

        if (!BuildConfig.DEBUG) {
            finish();
        }


        mViewModel.getChatQueue().offer("感谢你愿意陪着我");
        mViewModel.getCode();
        Logger.d("isHitagi = ", IzukoHelper.isHitagi(CipherUtils.encrypt(AppUtils.getAndroidId(this))));
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ChatAdapter adapter = new ChatAdapter();
        mBinding.rvChat.setAdapter(adapter);
        mBinding.rvChat.setLayoutManager(new SmoothScrollLayoutManager(this));
        mViewModel.setAdapter(adapter);

        mBinding.ibSend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mBinding.etChat.getText())) {
                mViewModel.addChat(mBinding.etChat.getText().toString(), ChatAdapter.TYPE_RIGHT);
                mBinding.etChat.setText("");
            }
        });
    }
}
