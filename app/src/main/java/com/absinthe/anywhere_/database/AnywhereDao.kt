package com.absinthe.anywhere_.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.model.PageEntity

@Dao
interface AnywhereDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ae: AnywhereEntity?)

    @Update
    fun update(ae: AnywhereEntity?)

    @Query("DELETE FROM anywhere_table")
    fun deleteAll()

    @Delete
    fun delete(ae: AnywhereEntity?)

    @get:Query("SELECT * from anywhere_table ORDER BY time_stamp DESC")
    val allAnywhereEntitiesOrderByTimeDesc: LiveData<List<AnywhereEntity>?>?

    @get:Query("SELECT * from anywhere_table ORDER BY time_stamp ASC")
    val allAnywhereEntitiesOrderByTimeAsc: LiveData<List<AnywhereEntity>?>?

    @get:Query("SELECT * from anywhere_table ORDER BY app_name DESC")
    val allAnywhereEntitiesOrderByNameDesc: LiveData<List<AnywhereEntity>?>?

    @get:Query("SELECT * from anywhere_table ORDER BY app_name ASC")
    val allAnywhereEntitiesOrderByNameAsc: LiveData<List<AnywhereEntity>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPage(pe: PageEntity?)

    @Update
    fun updatePage(pe: PageEntity?)

    @Delete
    fun deletePage(pe: PageEntity?)

    @get:Query("SELECT * from page_table ORDER BY priority ASC")
    val allPageEntities: LiveData<List<PageEntity>?>?
}