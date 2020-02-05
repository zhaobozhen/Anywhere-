package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.manager.SmoothScrollLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityGiftBinding;
import com.absinthe.anywhere_.model.GiftChatString;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
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

        mViewModel.getChatQueue().offer(GiftChatString.chats);
        Logger.d(AppUtils.getAndroidId(this));
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        mViewModel.stopOffer();
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
            String content = mBinding.etChat.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                if (com.absinthe.anywhere_.utils.TextUtils.isGiftCode(content)) {
                    mViewModel.getCode(content);
                } else {
                    mViewModel.responseChat();
                }
                mViewModel.addChat(content, ChatAdapter.TYPE_RIGHT);
                mBinding.etChat.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gift_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.toolbar_gift) {
            ToastUtil.makeText("Gift");
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
