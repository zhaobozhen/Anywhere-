package com.absinthe.anywhere_.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.gift.RightChatNode;
import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.model.ChatQueue;
import com.absinthe.anywhere_.ui.gift.GiftActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GiftViewModel extends AndroidViewModel {

    private ChatQueue mChatQueue;
    private ChatAdapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public GiftViewModel(@NonNull Application application) {
        super(application);
        mChatQueue = new ChatQueue(new ChatQueue.IChatQueueListener() {
            @Override
            public void onEnqueue() {
                while (!mChatQueue.isEmpty()) {
                    addChat(mChatQueue.poll(), ChatAdapter.TYPE_LEFT);
                }
            }

            @Override
            public void onDequeue(String head) {

            }
        });
    }

    public ChatAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(ChatAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public ChatQueue getChatQueue() {
        return mChatQueue;
    }

    public void getCode() {
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
                    List<GiftModel> giftModelList = new Gson().fromJson(result, new TypeToken<List<GiftModel>>() {}.getType());
                    if (giftModelList != null && giftModelList.size() >= 1) {
                        GiftModel giftModel = giftModelList.get(0);
                        if (giftModel.getIsActive() == 0 && giftModel.getSsaid().equals(AppUtils.getAndroidId(getApplication()))) {
                            mChatQueue.offer(giftModel.getSsaid());
                            mChatQueue.offer(giftModel.getAlipayAccount());
                            mChatQueue.offer(giftModel.getCode());
                            mChatQueue.offer(giftModel.getTimeStamp());
                            mChatQueue.offer("感谢你的心意♥");
                        }
                    }
                    response.body().close();
                } else {
                    Logger.d("Failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addChat(String msg, int type) {
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
        if (node instanceof RightChatNode) {
            mAdapter.addData(node);
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                GiftActivity.getInstance().getBinding().toolbar.toolbar.setTitle("Typing…");
                int delay = new Random().nextInt(1000) + 1000;
                mHandler.postDelayed(() -> {
                    mAdapter.addData(node);
                    GiftActivity.getInstance().getBinding().toolbar.toolbar.setTitle(R.string.settings_gift);
                }, delay);
            });
        }
    }
}
