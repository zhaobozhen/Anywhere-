package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.manager.SmoothScrollLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityGiftBinding;
import com.absinthe.anywhere_.model.GiftChatString;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.viewmodel.GiftViewModel;

import java.util.Random;

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

        mViewModel.getPrice();
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
        SmoothScrollLayoutManager manager = new SmoothScrollLayoutManager(this);
        mBinding.rvChat.setLayoutManager(manager);
        mBinding.rvChat.setHasFixedSize(true);
        mBinding.rvChat.setNestedScrollingEnabled(false);
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

        mViewModel.getNode().observe(this, node -> {
            if (node instanceof LeftChatNode) {
                mBinding.toolbar.toolbar.setTitle(R.string.settings_gift_typing);
                int delay = new Random().nextInt(500) + 1000;
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mViewModel.getAdapter().addData(node);
                    try {
                        Thread.sleep(50);
                        mBinding.rvChat.smoothScrollToPosition(mViewModel.getAdapter().getItemCount() - 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBinding.toolbar.toolbar.setTitle(R.string.settings_gift);
                }, delay);
            } else {
                mViewModel.getAdapter().addData(node);
                try {
                    Thread.sleep(50);
                    mBinding.rvChat.smoothScrollToPosition(mViewModel.getAdapter().getItemCount() - 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            DialogManager.showGiftPriceDialog(this);
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
