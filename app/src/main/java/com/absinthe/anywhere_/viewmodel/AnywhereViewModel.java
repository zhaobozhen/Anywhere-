package com.absinthe.anywhere_.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereRepository;
import com.absinthe.anywhere_.model.SerializableAnywhereEntity;

import java.util.List;

public class AnywhereViewModel extends AndroidViewModel {

    private AnywhereRepository mRepository;
    private LiveData<List<AnywhereEntity>> mAllAnywhereEntities;

    private MutableLiveData<String> mCommand = null;
    private MutableLiveData<String> mWorkingMode = null;
    private MutableLiveData<String> mBackground = null;

    public AnywhereViewModel(Application application) {
        super(application);
        mRepository = new AnywhereRepository(application);
        mAllAnywhereEntities = mRepository.getAllAnywhereEntities();
    }

    public LiveData<List<AnywhereEntity>> getAllAnywhereEntities() {
        return mAllAnywhereEntities;
    }

    public void insert(AnywhereEntity ae) {
        mRepository.insert(ae);
    }

    public void insert(SerializableAnywhereEntity sae) {
        AnywhereEntity ae = new AnywhereEntity(sae.getmId(),
                sae.getmAppName(), sae.getmParam1(),
                sae.getmParam2(), sae.getmParam3(),
                sae.getmDescription(), sae.getmType(),
                sae.getmTimeStamp());
        mRepository.insert(ae);
    }

    public void update(AnywhereEntity ae) {
        mRepository.update(ae);
    }

    public void delete(AnywhereEntity ae) {
        mRepository.delete(ae);
    }

    public MutableLiveData<String> getCommand() {
        if (mCommand == null) {
            mCommand = new MutableLiveData<>();
        }
        return mCommand;
    }

    public MutableLiveData<String> getWorkingMode() {
        if (mWorkingMode == null) {
            mWorkingMode = new MutableLiveData<>();
        }
        return mWorkingMode;
    }

    public MutableLiveData<String> getBackground() {
        if (mBackground == null) {
            mBackground = new MutableLiveData<>();
        }
        return mBackground;
    }

    public void refreshList() {
        mRepository.refreshList();
        mAllAnywhereEntities = mRepository.getAllAnywhereEntities();
    }
}
