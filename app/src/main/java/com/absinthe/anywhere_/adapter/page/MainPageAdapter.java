package com.absinthe.anywhere_.adapter.page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPageAdapter extends FragmentStateAdapter {

    private List<MutableLiveData<List<AnywhereEntity>>> mList;
    private List<MainFragment> fragments;

    public MainPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        mList = new ArrayList<>();
        fragments = new ArrayList<>();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        MainFragment fragment = MainFragment.newInstance(mList.get(position));
        fragments.add(fragment);
        return fragment;
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<MutableLiveData<List<AnywhereEntity>>> getList() {
        return mList;
    }

    public void setList(List<MutableLiveData<List<AnywhereEntity>>> mList) {
        this.mList = mList;
        for (int iter = 0, len = fragments.size(); iter < len; iter++) {
            fragments.get(iter).getList().setValue(mList.get(iter).getValue());
        }
        notifyDataSetChanged();
    }
}
