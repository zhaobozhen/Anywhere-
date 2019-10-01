package com.absinthe.anywhere_.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnywhereDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AnywhereEntity ae);

    @Query("DELETE FROM anywhere_table")
    void deleteAll();

    @Delete
    void delete(AnywhereEntity ae);

    @Query("SELECT * from anywhere_table ORDER BY time_stamp DESC")
    LiveData<List<AnywhereEntity>> getAllAnywhereEntities();
}
