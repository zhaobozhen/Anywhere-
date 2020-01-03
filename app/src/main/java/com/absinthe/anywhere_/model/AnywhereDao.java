package com.absinthe.anywhere_.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnywhereDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AnywhereEntity ae);

    @Update
    void update(AnywhereEntity ae);

    @Query("DELETE FROM anywhere_table")
    void deleteAll();

    @Delete
    void delete(AnywhereEntity ae);

    @Query("SELECT * from anywhere_table ORDER BY time_stamp DESC")
    LiveData<List<AnywhereEntity>> getAllAnywhereEntitiesOrderByTimeDesc();

    @Query("SELECT * from anywhere_table ORDER BY time_stamp ASC")
    LiveData<List<AnywhereEntity>> getAllAnywhereEntitiesOrderByTimeAsc();

    @Query("SELECT * from anywhere_table ORDER BY app_name DESC")
    LiveData<List<AnywhereEntity>> getAllAnywhereEntitiesOrderByNameDesc();

    @Query("SELECT * from anywhere_table ORDER BY app_name ASC")
    LiveData<List<AnywhereEntity>> getAllAnywhereEntitiesOrderByNameAsc();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPage(PageEntity pe);

    @Update
    void updatePage(PageEntity pe);

    @Delete
    void deletePage(PageEntity pe);

    @Query("SELECT * from page_table ORDER BY priority ASC")
    LiveData<List<PageEntity>> getAllPageEntities();
}
