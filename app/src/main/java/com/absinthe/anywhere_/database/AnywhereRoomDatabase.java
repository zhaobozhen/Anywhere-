package com.absinthe.anywhere_.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.PageEntity;

@Database(entities = {AnywhereEntity.class, PageEntity.class}, version = 7, exportSchema = false)
public abstract class AnywhereRoomDatabase extends RoomDatabase {

    public abstract AnywhereDao anywhereDao();

    private static AnywhereRoomDatabase INSTANCE;

    public static AnywhereRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AnywhereRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AnywhereRoomDatabase.class, "anywhere_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE anywhere_new (id TEXT NOT NULL, app_name TEXT NOT NULL, param_1 TEXT NOT NULL, param_2 TEXT, param_3 TEXT, description TEXT, type INTEGER NOT NULL DEFAULT 0, time_stamp TEXT NOT NULL, PRIMARY KEY(id))");
            // Copy the data
            database.execSQL(
                    "INSERT INTO anywhere_new (id, app_name, param_1, param_2, param_3, description, type, time_stamp) SELECT time_stamp, app_name, param_1, param_2, param_3, description, type, time_stamp FROM anywhere_table");
            // Remove the old table
            database.execSQL("DROP TABLE anywhere_table");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE anywhere_new RENAME TO anywhere_table");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE anywhere_table SET param_3=null");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anywhere_table "
                    + " ADD COLUMN category TEXT");

            //Create Page table
            database.execSQL(
                    "CREATE TABLE page_table (title TEXT NOT NULL, priority INTEGER NOT NULL, time_stamp TEXT NOT NULL, PRIMARY KEY(title))");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE page_new (id TEXT NOT NULL, title TEXT NOT NULL, priority INTEGER NOT NULL, time_stamp TEXT NOT NULL, PRIMARY KEY(id))");
            // Copy the data
            database.execSQL(
                    "INSERT INTO page_new (id, title, priority, time_stamp) SELECT time_stamp, title, priority, time_stamp FROM page_table");
            // Remove the old table
            database.execSQL("DROP TABLE page_table");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE page_new RENAME TO page_table");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anywhere_table "
                    + " ADD COLUMN color INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE page_new (id TEXT NOT NULL, title TEXT NOT NULL, priority INTEGER NOT NULL, type INTEGER NOT NULL, time_stamp TEXT NOT NULL, extra TEXT, PRIMARY KEY(id))");
            // Copy the data
            database.execSQL(
                    "INSERT INTO page_new (id, title, priority, type, time_stamp) SELECT id, title, priority, 0, time_stamp FROM page_table");
            // Remove the old table
            database.execSQL("DROP TABLE page_table");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE page_new RENAME TO page_table");
        }
    };
}
