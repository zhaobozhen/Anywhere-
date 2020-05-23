package com.absinthe.anywhere_.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity

@Database(entities = [AnywhereEntity::class, PageEntity::class], version = 9, exportSchema = false)
abstract class AnywhereRoomDatabase : RoomDatabase() {

    abstract fun anywhereDao(): AnywhereDao

    companion object {

        @Volatile
        private var INSTANCE: AnywhereRoomDatabase? = null

        fun getDatabase(context: Context): AnywhereRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AnywhereRoomDatabase::class.java,
                                "anywhere_database")
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3,
                                MIGRATION_3_4, MIGRATION_4_5,
                                MIGRATION_5_6, MIGRATION_6_7,
                                MIGRATION_7_8, MIGRATION_8_9)
                        .build()
                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                        "CREATE TABLE anywhere_new (id TEXT NOT NULL, app_name TEXT NOT NULL, param_1 TEXT NOT NULL, param_2 TEXT, param_3 TEXT, description TEXT, type INTEGER NOT NULL DEFAULT 0, time_stamp TEXT NOT NULL, PRIMARY KEY(id))")
                // Copy the data
                database.execSQL(
                        "INSERT INTO anywhere_new (id, app_name, param_1, param_2, param_3, description, type, time_stamp) SELECT time_stamp, app_name, param_1, param_2, param_3, description, type, time_stamp FROM anywhere_table")
                // Remove the old table
                database.execSQL("DROP TABLE anywhere_table")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE anywhere_new RENAME TO anywhere_table")
            }
        }
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("UPDATE anywhere_table SET param_3=null")
            }
        }
        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE anywhere_table "
                        + " ADD COLUMN category TEXT")

                //Create Page table
                database.execSQL(
                        "CREATE TABLE page_table (title TEXT NOT NULL, priority INTEGER NOT NULL, time_stamp TEXT NOT NULL, PRIMARY KEY(title))")
            }
        }
        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                        "CREATE TABLE page_new (id TEXT NOT NULL, title TEXT NOT NULL, priority INTEGER NOT NULL, time_stamp TEXT NOT NULL, PRIMARY KEY(id))")
                // Copy the data
                database.execSQL(
                        "INSERT INTO page_new (id, title, priority, time_stamp) SELECT time_stamp, title, priority, time_stamp FROM page_table")
                // Remove the old table
                database.execSQL("DROP TABLE page_table")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE page_new RENAME TO page_table")
            }
        }
        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE anywhere_table "
                        + " ADD COLUMN color INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                        "CREATE TABLE page_new (id TEXT NOT NULL, title TEXT NOT NULL, priority INTEGER NOT NULL, type INTEGER NOT NULL, time_stamp TEXT NOT NULL, extra TEXT, PRIMARY KEY(id))")
                // Copy the data
                database.execSQL(
                        "INSERT INTO page_new (id, title, priority, type, time_stamp) SELECT id, title, priority, 0, time_stamp FROM page_table")
                // Remove the old table
                database.execSQL("DROP TABLE page_table")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE page_new RENAME TO page_table")
            }
        }
        private val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE page_table "
                        + " ADD COLUMN backgroundUri TEXT")
            }
        }
        private val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE anywhere_table "
                        + " ADD COLUMN iconUri TEXT")
            }
        }
    }
}