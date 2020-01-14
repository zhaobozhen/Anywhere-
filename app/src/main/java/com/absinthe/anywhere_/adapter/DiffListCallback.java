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
        return TextUtils.equals(mOldList.get(oldItemPosition).getId(), mNewList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        AnywhereEntity oldItem = mOldList.get(oldItemPosition);
        AnywhereEntity newItem = mNewList.get(newItemPosition);

        return TextUtils.equals(oldItem.getAppName(), newItem.getAppName()) &&
                TextUtils.equals(oldItem.getParam1(), newItem.getParam1()) &&
                TextUtils.equals(oldItem.getParam2(), newItem.getParam2()) &&
                TextUtils.equals(oldItem.getParam3(), newItem.getParam3()) &&
                TextUtils.equals(oldItem.getDescription(), newItem.getDescription()) &&
                oldItem.getType().equals(newItem.getType());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}