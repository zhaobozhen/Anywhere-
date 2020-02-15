package com.absinthe.anywhere_.cloud.model;

import com.google.gson.annotations.SerializedName;

public class GiftPriceModel {

    @SerializedName("thirdTimesGift")
    private int thirdTimesGiftPrice;

    @SerializedName("infinityGift")
    private int infinityGiftPrice;

    public int getThirdTimesGiftPrice() {
        return thirdTimesGiftPrice;
    }

    public void setThirdTimesGiftPrice(int thirdTimesGiftPrice) {
        this.thirdTimesGiftPrice = thirdTimesGiftPrice;
    }

    public int getInfinityGiftPrice() {
        return infinityGiftPrice;
    }

    public void setInfinityGiftPrice(int infinityGiftPrice) {
        this.infinityGiftPrice = infinityGiftPrice;
    }
}
