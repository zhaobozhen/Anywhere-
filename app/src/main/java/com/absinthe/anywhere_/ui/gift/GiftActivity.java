package com.absinthe.anywhere_.ui.gift;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.BuildConfig;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.gift.RightChatNode;
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager;
import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.databinding.ActivityGiftBinding;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GiftActivity extends BaseActivity {

    private ActivityGiftBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gift);

        initView();

        if (!BuildConfig.DEBUG) {
            finish();
        }

        FormBody formBody = new FormBody.Builder()
                .add("code", "00000-00000-00000-00000")
                .build();

        Request request = new Request.Builder()
                .url(URLManager.GIFT_SCF_URL)
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Logger.d(result);
                    List<GiftModel> giftModelList = new Gson().fromJson(result, new TypeToken<List<GiftModel>>() {}.getType());
                    if (giftModelList != null && giftModelList.size() >= 1) {
                        GiftModel giftModel = giftModelList.get(0);
                        if (giftModel.getIsActive() == 0 && giftModel.getSsaid().equals(AppUtils.getAndroidId(this))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.makeText("Active!");
                                }
                            });
                        }
                    }
                    Logger.d(result);
                    response.body().close();
                } else {
                    Logger.d("Failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ChatAdapter adapter = new ChatAdapter();
        mBinding.rvChat.setAdapter(adapter);
        mBinding.rvChat.setLayoutManager(new WrapContentLinearLayoutManager(this));

        List<BaseNode> list = new ArrayList<>();
        LeftChatNode leftChatNode = new LeftChatNode();
        leftChatNode.setMsg("LeftLeftLeftLeftLeftLeftLeftLeft");
        list.add(leftChatNode);

        RightChatNode rightChatNode = new RightChatNode();
        rightChatNode.setMsg("RightRightRightRightRightRight");
        list.add(rightChatNode);
        adapter.setNewData(list);
    }
}
