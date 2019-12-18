package com.absinthe.anywhere_.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.absinthe.anywhere_.model.AnywhereEntity;

import java.util.List;

public class DiffListCallback extends DiffUtil.Callback {

    private List<AnywhereEntity> oldList;
    private List<AnywhereEntity> newList;

    DiffListCallback(List<AnywhereEntity> newList, List<AnywhereEntity> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return TextUtils.equals(oldList.get(oldItemPosition).getAppName(), newList.get(newItemPosition).getAppName()) &&
                TextUtils.equals(oldList.get(oldItemPosition).getParam1(), newList.get(newItemPosition).getParam1()) &&
                TextUtils.equals(oldList.get(oldItemPosition).getParam2(), newList.get(newItemPosition).getParam2()) &&
                TextUtils.equals(oldList.get(oldItemPosition).getParam3(), newList.get(newItemPosition).getParam3()) &&
                TextUtils.equals(oldList.get(oldItemPosition).getDescription(), newList.get(newItemPosition).getDescription()) &&
                TextUtils.equals(oldList.get(oldItemPosition).getType() + "", newList.get(newItemPosition).getType() + "");
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}