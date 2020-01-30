package com.absinthe.anywhere_.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.absinthe.anywhere_.cloud.model.GiftModel;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GiftViewModel extends AndroidViewModel {

    private MutableLiveData<String> mMessage = null;

    public GiftViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getMessage() {
        if (mMessage == null) {
            mMessage = new MutableLiveData<>();
        }
        return mMessage;
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
                            new Handler(Looper.getMainLooper()).post(() -> getMessage().setValue(giftModel.getSsaid()));
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
}
