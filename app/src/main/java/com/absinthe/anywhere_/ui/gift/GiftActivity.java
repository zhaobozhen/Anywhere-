package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.gift.RightChatNode;
import com.absinthe.anywhere_.adapter.manager.SmoothScrollLayoutManager;
import com.absinthe.anywhere_.databinding.ActivityGiftBinding;
import com.absinthe.anywhere_.viewmodel.GiftViewModel;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.Random;

public class GiftActivity extends BaseActivity {

    private ActivityGiftBinding mBinding;
    private GiftViewModel mViewModel;
    private ChatAdapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gift);
        mViewModel = new ViewModelProvider(this).get(GiftViewModel.class);

        initView();

        if (!BuildConfig.DEBUG) {
            finish();
        }

        mViewModel.getMessage().observe(this, s -> addChat(s, ChatAdapter.TYPE_LEFT));

        mViewModel.getMessage().setValue("感谢你愿意陪着我");
        mViewModel.getMessage().setValue("如果你送我一件礼物，我也会给你一件珍贵的礼物哦");

        mViewModel.getCode();
    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new ChatAdapter();
        mBinding.rvChat.setAdapter(mAdapter);
        mBinding.rvChat.setLayoutManager(new SmoothScrollLayoutManager(this));
    }

    private void addChat(String msg, int type) {
        if (type == ChatAdapter.TYPE_LEFT) {
            LeftChatNode node = new LeftChatNode();
            node.setMsg(msg);
            addNode(node);
        } else {
            RightChatNode node = new RightChatNode();
            node.setMsg(msg);
            addNode(node);
        }
    }

    private void addNode(BaseNode node) {
        mBinding.toolbar.toolbar.setTitle("Typing…");
        int delay = new Random().nextInt(1000) + 1000;
        mHandler.postDelayed(() -> {
            mAdapter.addData(node);
            mBinding.toolbar.toolbar.setTitle(R.string.settings_gift);
        }, delay);
    }
}
