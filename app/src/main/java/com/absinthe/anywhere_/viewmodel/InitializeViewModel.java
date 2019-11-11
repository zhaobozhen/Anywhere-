package com.absinthe.anywhere_.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InitializeViewModel extends ViewModel {

    public static final int ROOT_PERM = 1;
    public static final int SHIZUKU_CHECK_PERM = 2;
    public static final int SHIZUKU_PERM = 4;
    public static final int OVERLAY_PERM = 8;
    public static final int SHIZUKU_GROUP_PERM = 6;
    private MutableLiveData<Boolean> isRoot;
    private MutableLiveData<Boolean> isOverlay;
    private MutableLiveData<Boolean> isPopup;
    private MutableLiveData<Boolean> isShizukuCheck;
    private MutableLiveData<Boolean> isShizuku;
    private MutableLiveData<Integer> allPerm;

    public MutableLiveData<Boolean> getIsRoot() {
        if (isRoot == null) {
            isRoot = new MutableLiveData<>();
        }
        return isRoot;
    }

    public MutableLiveData<Boolean> getIsOverlay() {
        if (isOverlay == null) {
            isOverlay = new MutableLiveData<>();
        }
        return isOverlay;
    }

    public MutableLiveData<Boolean> getIsPopup() {
        if (isPopup == null) {
            isPopup = new MutableLiveData<>();
        }
        return isPopup;
    }

    public MutableLiveData<Boolean> getIsShizukuCheck() {
        if (isShizukuCheck == null) {
            isShizukuCheck = new MutableLiveData<>();
        }
        return isShizukuCheck;
    }

    public MutableLiveData<Boolean> getIsShizuku() {
        if (isShizuku == null) {
            isShizuku = new MutableLiveData<>();
        }
        return isShizuku;
    }

    public MutableLiveData<Integer> getAllPerm() {
        if (allPerm == null) {
            allPerm = new MutableLiveData<>();
            allPerm.setValue(0);
        }
        return allPerm;
    }

}
