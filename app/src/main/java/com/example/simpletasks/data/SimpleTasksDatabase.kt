@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection"
)

package com.example.simpletasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simpletasks.data.settings.Settings
import com.example.simpletasks.data.settings.SettingsDao
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoDao
import com.example.simpletasks.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Todo::class, Settings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SimpleTasksDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    abstract fun settingsDao(): SettingsDao

    private class SimpleTasksDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.settingsDao())
                }
            }
        }

        suspend fun populateDatabase(preferencesDao: SettingsDao) {
            preferencesDao.deleteAllSettings()
            val sampleSettings = Settings.Default
            preferencesDao.addSettings(sampleSettings)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SimpleTasksDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SimpleTasksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimpleTasksDatabase::class.java,
                    "simple_tasks_database"
                ).addCallback(SimpleTasksDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }
}