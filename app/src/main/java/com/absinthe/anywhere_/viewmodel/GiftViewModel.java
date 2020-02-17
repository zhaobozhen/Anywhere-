package com.absinthe.anywhere_.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.absinthe.anywhere_.adapter.gift.ChatAdapter;
import com.absinthe.anywhere_.adapter.gift.InfoNode;
import com.absinthe.anywhere_.adapter.gift.LeftChatNode;
import com.absinthe.anywhere_.adapter.gift.RightChatNode;
import com.absinthe.anywhere_.cloud.GiftStatusCode;
import com.absinthe.anywhere_.cloud.interfaces.GiftRequest;
import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.cloud.model.GiftPriceModel;
import com.absinthe.anywhere_.model.ChatQueue;
import com.absinthe.anywhere_.model.GiftChatString;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.CipherUtils;
import com.absinthe.anywhere_.utils.StorageUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GiftViewModel extends AndroidViewModel {

    private ChatQueue mChatQueue;
    private ChatAdapter mAdapter;
    private MutableLiveData<BaseNode> mNode = new MutableLiveData<>();
    private MutableLiveData<Integer> mThirdTimesPrice = new MutableLiveData<>();
    private MutableLiveData<Integer> mInfinityPrice = new MutableLiveData<>();

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

    public MutableLiveData<BaseNode> getNode() {
        return mNode;
    }

    public MutableLiveData<Integer> getThirdTimesPrice() {
        return mThirdTimesPrice;
    }

    public MutableLiveData<Integer> getInfinityPrice() {
        return mInfinityPrice;
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

        gift.enqueue(new Callback<GiftModel>() {
            @Override
            public void onResponse(@NonNull Call<GiftModel> call, @NonNull Response<GiftModel> response) {
                GiftModel giftModel = response.body();
                if (giftModel != null) {
                    if (giftModel.getStatusCode() == GiftStatusCode.STATUS_SUCCESS) {
                        GiftModel.Data data = giftModel.getData();
                        if (data == null) {
                            Logger.d("data == null");
                            return;
                        }
                        if (data.isActive == 0) {
                            mChatQueue.clear();
                            mChatQueue.offer(GiftChatString.purchaseResponse);

                            String encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()));
                            try {
                                StorageUtils.storageToken(getApplication(), encode);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (data.isActive == 1 && data.ssaid.equals(AppUtils.getAndroidId(getApplication()))) {
                            mChatQueue.offer(GiftChatString.hasPurchasedResponse);

                            String encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()));
                            try {
                                StorageUtils.storageToken(getApplication(), encode);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (data.isActive == 1 && !data.ssaid.equals(AppUtils.getAndroidId(getApplication()))) {
                            mChatQueue.offer(GiftChatString.notYourCodeResponse);
                        }
                    } else if (giftModel.getStatusCode() == GiftStatusCode.STATUS_NO_MATCH_DATA) {
                        mChatQueue.offer(GiftChatString.notExistCodeResponse);
                    } else {
                        mChatQueue.offer(GiftChatString.abnormalResponse);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GiftModel> call, @NonNull Throwable t) {
                Logger.d("Failed:", t.getMessage());
            }
        });
    }

    public void getPrice() {
        if (mThirdTimesPrice.getValue() != null && mInfinityPrice.getValue() != null) {
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLManager.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GiftRequest request = retrofit.create(GiftRequest.class);
        Call<GiftPriceModel> price = request.requestPrice();

        price.enqueue(new Callback<GiftPriceModel>() {
            @Override
            public void onResponse(@NonNull Call<GiftPriceModel> call, @NonNull Response<GiftPriceModel> response) {
                GiftPriceModel priceModel = response.body();
                if (priceModel != null) {
                    mThirdTimesPrice.setValue(priceModel.getThirdTimesGiftPrice());
                    mInfinityPrice.setValue(priceModel.getInfinityGiftPrice());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GiftPriceModel> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void responseChat() {
        mChatQueue.offer(GiftChatString.leisureResponse);
    }

    public void stopOffer() {
        mChatQueue.stopOffer();
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
        new Handler(Looper.getMainLooper()).post(() -> mNode.setValue(node));
    }
}
