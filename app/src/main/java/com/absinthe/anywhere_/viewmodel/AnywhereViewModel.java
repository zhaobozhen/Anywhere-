package com.absinthe.anywhere_.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnywhereViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mPackageName = null;
    private MutableLiveData<String> mClassName = null;

    public MutableLiveData<String> getPackageName() {
        if (mPackageName == null) {
            mPackageName = new MutableLiveData<>();
        }
        return mPackageName;
    }

    public MutableLiveData<String> getClassName() {
        if (mClassName == null) {
            mClassName = new MutableLiveData<>();
        }
        return mClassName;
    }
}
