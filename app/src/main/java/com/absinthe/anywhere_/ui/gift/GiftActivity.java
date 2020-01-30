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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiftActivity extends BaseActivity {

    private ActivityGiftBinding mBinding;
    private GiftViewModel mViewModel;
    private ChatAdapter mAdapter;

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

        List<BaseNode> list = new ArrayList<>();
        LeftChatNode leftChatNode = new LeftChatNode();
        leftChatNode.setMsg("LeftLeftLeftLeftLeftLeftLeftLeft");
        list.add(leftChatNode);

        RightChatNode rightChatNode = new RightChatNode();
        rightChatNode.setMsg("RightRightRightRightRightRight");
        list.add(rightChatNode);
        mAdapter.setNewData(list);
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mAdapter.addData(node);
            mBinding.toolbar.toolbar.setTitle(R.string.settings_gift);
        }, delay);
    }
}
