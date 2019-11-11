package com.absinthe.anywhere_.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AnywhereRepository {
    private AnywhereDao mAnywhereDao;
    private LiveData<List<AnywhereEntity>> mAllAnywhereEntities;

    public AnywhereRepository(Application application) {
        AnywhereRoomDatabase db = AnywhereRoomDatabase.getDatabase(application);
        mAnywhereDao = db.anywhereDao();
        mAllAnywhereEntities = getSortedEntities();
    }

    public LiveData<List<AnywhereEntity>> getAllAnywhereEntities() {
        return mAllAnywhereEntities;
    }

    public void insert(AnywhereEntity ae) {
        new insertAsyncTask(mAnywhereDao).execute(ae);
    }

    public void update(AnywhereEntity ae) {
        new updateAsyncTask(mAnywhereDao).execute(ae);
    }

    public void delete(AnywhereEntity ae) {
        new deleteAsyncTask(mAnywhereDao).execute(ae);
    }

    private LiveData<List<AnywhereEntity>> getSortedEntities() {
        switch (GlobalValues.sSortMode) {
            case Const.SORT_MODE_TIME_ASC:
                return mAnywhereDao.getAllAnywhereEntitiesOrderByTimeAsc();
            case Const.SORT_MODE_NAME_ASC:
                return mAnywhereDao.getAllAnywhereEntitiesOrderByNameAsc();
            case Const.SORT_MODE_NAME_DESC:
                return mAnywhereDao.getAllAnywhereEntitiesOrderByNameDesc();
            case Const.SORT_MODE_TIME_DESC:
            default:
                return mAnywhereDao.getAllAnywhereEntitiesOrderByTimeDesc();
        }
    }

    private static class insertAsyncTask extends AsyncTask<AnywhereEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        insertAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AnywhereEntity... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<AnywhereEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        updateAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AnywhereEntity... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<AnywhereEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        deleteAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AnywhereEntity... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}
