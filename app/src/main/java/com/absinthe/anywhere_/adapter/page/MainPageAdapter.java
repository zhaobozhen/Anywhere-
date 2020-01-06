package com.absinthe.anywhere_.adapter.page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPageAdapter extends FragmentStateAdapter {

    private List<MutableLiveData<List<AnywhereEntity>>> mList;

    public MainPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        mList = new ArrayList<>();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MainFragment.newInstance(mList.get(position));
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
        notifyDataSetChanged();
    }
}
