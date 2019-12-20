package com.absinthe.anywhere_.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.absinthe.anywhere_.model.AnywhereEntity;

import java.util.List;

public class DiffListCallback extends DiffUtil.Callback {

    private List<AnywhereEntity> mOldList;
    private List<AnywhereEntity> mNewList;

    DiffListCallback(List<AnywhereEntity> newList, List<AnywhereEntity> oldList) {
        this.mNewList = newList;
        this.mOldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getId().equals(mNewList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return TextUtils.equals(mOldList.get(oldItemPosition).getAppName(), mNewList.get(newItemPosition).getAppName()) &&
                TextUtils.equals(mOldList.get(oldItemPosition).getParam1(), mNewList.get(newItemPosition).getParam1()) &&
                TextUtils.equals(mOldList.get(oldItemPosition).getParam2(), mNewList.get(newItemPosition).getParam2()) &&
                TextUtils.equals(mOldList.get(oldItemPosition).getParam3(), mNewList.get(newItemPosition).getParam3()) &&
                TextUtils.equals(mOldList.get(oldItemPosition).getDescription(), mNewList.get(newItemPosition).getDescription()) &&
                TextUtils.equals(mOldList.get(oldItemPosition).getType() + "", mNewList.get(newItemPosition).getType() + "");
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}