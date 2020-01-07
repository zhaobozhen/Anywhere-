package com.absinthe.anywhere_.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.absinthe.anywhere_.ui.backup.BackupActivity;

import java.util.List;

public class AnywhereRepository {
    private AnywhereDao mAnywhereDao;
    private LiveData<List<PageEntity>> mAllPageEntities;
    private LiveData<List<AnywhereEntity>> mAllAnywhereEntities;

    public AnywhereRepository(Application application) {
        AnywhereRoomDatabase db = AnywhereRoomDatabase.getDatabase(application);
        mAnywhereDao = db.anywhereDao();
        mAllPageEntities = mAnywhereDao.getAllPageEntities();
        mAllAnywhereEntities = getSortedEntities();
    }

    public LiveData<List<AnywhereEntity>> getAllAnywhereEntities() {
        return mAllAnywhereEntities;
    }

    public LiveData<List<PageEntity>> getAllPageEntities() {
        return mAllPageEntities;
    }

    public LiveData<List<AnywhereEntity>> getSortedByTimeDesc() {
        return mAnywhereDao.getAllAnywhereEntitiesOrderByTimeDesc();
    }

    public LiveData<List<AnywhereEntity>> getSortedByTimeAsc() {
        return mAnywhereDao.getAllAnywhereEntitiesOrderByTimeAsc();
    }

    public LiveData<List<AnywhereEntity>> getSortedByNameDesc() {
        return mAnywhereDao.getAllAnywhereEntitiesOrderByNameDesc();
    }

    public LiveData<List<AnywhereEntity>> getSortedByNameAsc() {
        return mAnywhereDao.getAllAnywhereEntitiesOrderByNameAsc();
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

    private static class insertAsyncTask extends AsyncTask<AnywhereEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        insertAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AnywhereEntity... params) {
            try {
                mAsyncTaskDao.insert(params[0]);
            } catch (Exception e) {
                BackupActivity.INSERT_CORRECT = false;
            }
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

    public void insertPage(PageEntity pe) {
        new insertPageAsyncTask(mAnywhereDao).execute(pe);
    }

    public void updatePage(PageEntity pe) {
        new updatePageAsyncTask(mAnywhereDao).execute(pe);
    }

    public void deletePage(PageEntity pe) {
        new deletePageAsyncTask(mAnywhereDao).execute(pe);
    }

    private static class insertPageAsyncTask extends AsyncTask<PageEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        insertPageAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PageEntity... params) {
            mAsyncTaskDao.insertPage(params[0]);
            return null;
        }
    }

    private static class updatePageAsyncTask extends AsyncTask<PageEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        updatePageAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PageEntity... params) {
            mAsyncTaskDao.updatePage(params[0]);
            return null;
        }
    }

    private static class deletePageAsyncTask extends AsyncTask<PageEntity, Void, Void> {

        private AnywhereDao mAsyncTaskDao;

        deletePageAsyncTask(AnywhereDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PageEntity... params) {
            mAsyncTaskDao.deletePage(params[0]);
            return null;
        }
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
}
