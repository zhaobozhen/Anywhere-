package com.absinthe.anywhere_.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.model.PageEntity
import com.absinthe.anywhere_.ui.backup.BackupActivity

class AnywhereRepository(application: Application) {
    private val mAnywhereDao: AnywhereDao
    val allPageEntities: LiveData<List<PageEntity>?>?
    var allAnywhereEntities: LiveData<List<AnywhereEntity>?>?
        private set

    fun refresh() {
        allAnywhereEntities = sortedEntities
    }

    fun insert(ae: AnywhereEntity?) {
        InsertAsyncTask(mAnywhereDao).execute(ae)
    }

    fun update(ae: AnywhereEntity?) {
        UpdateAsyncTask(mAnywhereDao).execute(ae)
    }

    fun delete(ae: AnywhereEntity?) {
        DeleteAsyncTask(mAnywhereDao).execute(ae)
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<AnywhereEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: AnywhereEntity): Void? {
            try {
                mAsyncTaskDao.insert(params[0])
            } catch (e: Exception) {
                BackupActivity.INSERT_CORRECT = false
            }
            return null
        }

    }

    private class UpdateAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<AnywhereEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: AnywhereEntity): Void? {
            mAsyncTaskDao.update(params[0])
            return null
        }

    }

    private class DeleteAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<AnywhereEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: AnywhereEntity): Void? {
            mAsyncTaskDao.delete(params[0])
            return null
        }

    }

    fun insertPage(pe: PageEntity?) {
        InsertPageAsyncTask(mAnywhereDao).execute(pe)
    }

    fun updatePage(pe: PageEntity?) {
        UpdatePageAsyncTask(mAnywhereDao).execute(pe)
    }

    fun deletePage(pe: PageEntity?) {
        DeletePageAsyncTask(mAnywhereDao).execute(pe)
    }

    private class InsertPageAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<PageEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: PageEntity): Void? {
            mAsyncTaskDao.insertPage(params[0])
            return null
        }

    }

    private class UpdatePageAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<PageEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: PageEntity): Void? {
            mAsyncTaskDao.updatePage(params[0])
            return null
        }

    }

    private class DeletePageAsyncTask internal constructor(private val mAsyncTaskDao: AnywhereDao) : AsyncTask<PageEntity, Void?, Void?>() {
        override fun doInBackground(vararg params: PageEntity): Void? {
            mAsyncTaskDao.deletePage(params[0])
            return null
        }

    }

    private val sortedEntities: LiveData<List<AnywhereEntity>?>?
        get() = when (GlobalValues.sSortMode) {
            Const.SORT_MODE_TIME_ASC -> mAnywhereDao.allAnywhereEntitiesOrderByTimeAsc
            Const.SORT_MODE_NAME_ASC -> mAnywhereDao.allAnywhereEntitiesOrderByNameAsc
            Const.SORT_MODE_NAME_DESC -> mAnywhereDao.allAnywhereEntitiesOrderByNameDesc
            Const.SORT_MODE_TIME_DESC -> mAnywhereDao.allAnywhereEntitiesOrderByTimeDesc
            else -> mAnywhereDao.allAnywhereEntitiesOrderByTimeDesc
        }

    init {
        val db = AnywhereRoomDatabase.getDatabase(application)
        mAnywhereDao = db!!.anywhereDao()
        allPageEntities = mAnywhereDao.allPageEntities
        allAnywhereEntities = sortedEntities
    }
}