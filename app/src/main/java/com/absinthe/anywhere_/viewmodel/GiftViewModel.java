package com.absinthe.anywhere_.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.gift.InfoNode;
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.gift.RightChatNode;
import com.absinthe.anywhere_.cloud.GiftStatusCode;
import com.absinthe.anywhere_.cloud.interfaces.GiftRequest;
import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.model.ChatQueue;
import com.absinthe.anywhere_.model.GiftChatString;
import com.absinthe.anywhere_.ui.gift.GiftActivity;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.io.IOException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GiftViewModel extends AndroidViewModel {

    private ChatQueue mChatQueue;
    private ChatAdapter mAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public GiftViewModel(@NonNull Application application) {
        super(application);
        mChatQueue = new ChatQueue(new ChatQueue.IChatQueueListener() {
            @Override
            public void onEnqueue(int type) {
                addChat(mChatQueue.poll(), type);
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

    public void getCode(String code) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLManager.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GiftRequest request = retrofit.create(GiftRequest.class);
        Call<GiftModel> gift = request.requestByCode(code, AppUtils.getAndroidId(getApplication()));

        new Thread(() -> {
            try {
                Response<GiftModel> response = gift.execute();
                if (response.isSuccessful()) {
                    GiftModel giftModel = response.body();
                    if (giftModel != null) {
                        if (giftModel.getStatusCode() == GiftStatusCode.STATUS_SUCCESS) {
                            GiftModel.Data data = giftModel.getData(0);
                            if (data.getIsActive() == 0) {
                                mChatQueue.clear();
                                mChatQueue.offer(GiftChatString.purchaseResponse);

                                String encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()));
                                StorageUtils.storageToken(getApplication(), encode);
                            } else if (data.getIsActive() == 1 && data.getSsaid().equals(AppUtils.getAndroidId(getApplication()))) {
                                mChatQueue.offer(GiftChatString.hasPurchasedResponse);

                                String encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()));
                                StorageUtils.storageToken(getApplication(), encode);
                            } else if (data.getIsActive() == 1 && !data.getSsaid().equals(AppUtils.getAndroidId(getApplication()))) {
                                mChatQueue.offer(GiftChatString.notYourCodeResponse);
                            }
                        } else if (giftModel.getStatusCode() == GiftStatusCode.STATUS_NO_MATCH_DATA) {
                            mChatQueue.offer(GiftChatString.notYourCodeResponse);
                        } else {
                            mChatQueue.offer(GiftChatString.abnormalResponse);
                        }
                    }
                } else {
                    Logger.d("Failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void responseChat() {

    }

    public void addChat(String msg, int type) {
        if (type == ChatAdapter.TYPE_LEFT) {
            LeftChatNode node = new LeftChatNode();
            node.setMsg(msg);
            addNode(node);
        } else if (type == ChatAdapter.TYPE_RIGHT) {
            RightChatNode node = new RightChatNode();
            node.setMsg(msg);
            addNode(node);
        } else {
            InfoNode node = new InfoNode();
            node.setMsg(msg);
            addNode(node);
        }
    }

    private void addNode(BaseNode node) {
        if (node instanceof LeftChatNode) {
            if (GiftActivity.getInstance().getBinding() != null) {
                mHandler.post(() -> GiftActivity.getInstance().getBinding().toolbar.toolbar.setTitle(R.string.settings_gift_typing));
                int delay = new Random().nextInt(500) + 1000;
                mHandler.postDelayed(() -> {
                    mAdapter.addData(node);
                    GiftActivity.getInstance().getBinding().toolbar.toolbar.setTitle(R.string.settings_gift);
                }, delay);
            }
        } else {
            mHandler.post(() -> mAdapter.addData(node));
        }
    }
}
