package com.absinthe.anywhere_.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.PageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AnywhereRepository(application: Application) {

    var allAnywhereEntities: LiveData<List<AnywhereEntity>>
        private set
    val allPageEntities: LiveData<List<PageEntity>>

    private val mAnywhereDao: AnywhereDao = AnywhereRoomDatabase.getDatabase(application).anywhereDao()

    private val sortedEntities: LiveData<List<AnywhereEntity>>
        get() = when (GlobalValues.sortMode) {
            Const.SORT_MODE_TIME_ASC -> mAnywhereDao.allAnywhereEntitiesOrderByTimeAsc
            Const.SORT_MODE_NAME_ASC -> mAnywhereDao.allAnywhereEntitiesOrderByNameAsc
            Const.SORT_MODE_NAME_DESC -> mAnywhereDao.allAnywhereEntitiesOrderByNameDesc
            Const.SORT_MODE_TIME_DESC -> mAnywhereDao.allAnywhereEntitiesOrderByTimeDesc
            else -> mAnywhereDao.allAnywhereEntitiesOrderByTimeDesc
        }

    init {
        allPageEntities = mAnywhereDao.allPageEntities
        allAnywhereEntities = sortedEntities
    }

    fun refresh() {
        allAnywhereEntities = sortedEntities
    }

    fun insert(ae: AnywhereEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.insert(ae)
        }
        GlobalValues.needBackup = true
    }

    fun update(ae: AnywhereEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.update(ae)
        }
        GlobalValues.needBackup = true
    }

    fun delete(ae: AnywhereEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.delete(ae)
        }
        GlobalValues.needBackup = true
    }

    fun insertPage(pe: PageEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.insertPage(pe)
        }
        GlobalValues.needBackup = true
    }

    fun updatePage(pe: PageEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.updatePage(pe)
        }
        GlobalValues.needBackup = true
    }

    fun deletePage(pe: PageEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            mAnywhereDao.deletePage(pe)
        }
        GlobalValues.needBackup = true
    }
}