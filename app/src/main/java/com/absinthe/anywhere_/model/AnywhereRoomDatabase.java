package com.absinthe.anywhere_.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AnywhereEntity.class}, version = 1, exportSchema = false)
public abstract class AnywhereRoomDatabase extends RoomDatabase {

    public abstract AnywhereDao anywhereDao();
    private static AnywhereRoomDatabase INSTANCE;

    static AnywhereRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AnywhereRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AnywhereRoomDatabase.class, "anywhere_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
