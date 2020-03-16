package com.absinthe.anywhere_.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InitializeViewModel : ViewModel() {

    var isRoot: MutableLiveData<Boolean> = MutableLiveData()
    var isOverlay: MutableLiveData<Boolean> = MutableLiveData()
    var isPopup: MutableLiveData<Boolean> = MutableLiveData()
    var isShizukuCheck: MutableLiveData<Boolean> = MutableLiveData()
    var isShizuku: MutableLiveData<Boolean> = MutableLiveData()
    var allPerm: MutableLiveData<Int> = MutableLiveData()

    companion object {
        const val ROOT_PERM = 1
        const val SHIZUKU_CHECK_PERM = 2
        const val SHIZUKU_PERM = 4
        const val OVERLAY_PERM = 8
        const val SHIZUKU_GROUP_PERM = 6
    }
}