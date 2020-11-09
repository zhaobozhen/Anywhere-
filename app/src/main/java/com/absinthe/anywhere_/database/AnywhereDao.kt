package com.absinthe.anywhere_.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity

@Dao
interface AnywhereDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ae: AnywhereEntity)

    @Update
    suspend fun update(ae: AnywhereEntity)

    @Query("DELETE FROM anywhere_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(ae: AnywhereEntity)

    @get:Query("SELECT * from anywhere_table ORDER BY time_stamp DESC")
    val allAnywhereEntitiesOrderByTimeDesc: LiveData<List<AnywhereEntity>>

    @get:Query("SELECT * from anywhere_table ORDER BY time_stamp ASC")
    val allAnywhereEntitiesOrderByTimeAsc: LiveData<List<AnywhereEntity>>

    @get:Query("SELECT * from anywhere_table ORDER BY app_name DESC")
    val allAnywhereEntitiesOrderByNameDesc: LiveData<List<AnywhereEntity>>

    @get:Query("SELECT * from anywhere_table ORDER BY app_name ASC")
    val allAnywhereEntitiesOrderByNameAsc: LiveData<List<AnywhereEntity>>

    @Query("SELECT * from anywhere_table WHERE id LIKE :id")
    fun getEntityById(id: String): AnywhereEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(pe: PageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(pageList: List<PageEntity>)

    @Update
    suspend fun updatePage(pe: PageEntity)

    @Delete
    suspend fun deletePage(pe: PageEntity)

    @get:Query("SELECT * from page_table ORDER BY priority ASC")
    val allPageEntities: LiveData<List<PageEntity>>

    @Query("SELECT * from page_table WHERE title LIKE :title")
    fun getPageEntityByTitle(title: String): PageEntity?
}