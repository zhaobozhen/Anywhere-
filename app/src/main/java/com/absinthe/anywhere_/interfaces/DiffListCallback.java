package com.absinthe.anywhere_.interfaces;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.absinthe.anywhere_.model.AnywhereEntity;

import java.util.List;

public class DiffListCallback extends DiffUtil.Callback{

    private List<AnywhereEntity> oldList;
    private List<AnywhereEntity> newList;

    public DiffListCallback(List<AnywhereEntity> newList, List<AnywhereEntity> oldList) {
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
        return oldList.get(oldItemPosition).getAppName().equals(newList.get(newItemPosition).getAppName()) &&
                oldList.get(oldItemPosition).getParam1().equals(newList.get(newItemPosition).getParam1()) &&
                oldList.get(oldItemPosition).getParam2().equals(newList.get(newItemPosition).getParam2()) &&
                oldList.get(oldItemPosition).getDescription().equals(newList.get(newItemPosition).getDescription()) &&
                oldList.get(oldItemPosition).getType().equals(newList.get(newItemPosition).getType());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}