package com.mani.wirup

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Task::class, Note::class, Client::class],
    version = 12, // Incremented version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun clientDao(): ClientDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 11 to 12
        val MIGRATION_11_12 = object : Migration(11, 12) {
            @SuppressLint("Range")
            override fun migrate(database: SupportSQLiteDatabase) {
                // Check if the `clientId` column already exists
                val cursor = database.query("PRAGMA table_info(tasks)")
                var clientIdExists = false
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndex("name"))
                    if (columnName == "clientId") {
                        clientIdExists = true
                        break
                    }
                }
                cursor.close()

                // Add the `clientId` column only if it doesn't exist
                if (!clientIdExists) {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN clientId INTEGER")
                }

                // Check if the `duration` column already exists
                val cursor2 = database.query("PRAGMA table_info(tasks)")
                var durationExists = false
                while (cursor2.moveToNext()) {
                    val columnName = cursor2.getString(cursor2.getColumnIndex("name"))
                    if (columnName == "duration") {
                        durationExists = true
                        break
                    }
                }
                cursor2.close()

                // Add the `duration` column only if it doesn't exist
                if (!durationExists) {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_11_12) // Add the migration
                    .fallbackToDestructiveMigration() // Fallback to destructive migration if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}