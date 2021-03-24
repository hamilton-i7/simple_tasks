package com.example.simpletasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simpletasks.R
import com.example.simpletasks.data.settings.Settings
import com.example.simpletasks.data.settings.SettingsDao
import com.example.simpletasks.data.task.Task
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
                    populateDatabase(
                        database.todoDao(),
                        database.settingsDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(todoDao: TodoDao, preferencesDao: SettingsDao) {
            todoDao.deleteAllTodos()
            preferencesDao.deleteAllSettings()

            val sampleSettings = Settings(
                completedTasksExpanded = true
            )
            preferencesDao.addSettings(sampleSettings)

            var sampleTasks = listOf(
                Task(name = "Replay email"),
                Task(name = "Jogging"),
                Task(name = "Get up early"),
                Task(name = "Water the flower"),
                Task(name = "Read book", completed = true),
                Task(name = "Drink water", completed = true)
            )
            var sampleTodo = Todo(
                name = "Habit",
                colorResource = R.color.blue,
                tasks = sampleTasks
            )
            todoDao.addTodo(sampleTodo)

            sampleTasks = listOf(
                Task(name = "Review design"),
                Task(name = "Create prototype", completed = true),
                Task(name = "Call Michael", completed = true)
            )
            sampleTodo = Todo(
                name = "Work",
                tasks = sampleTasks
            )
            todoDao.addTodo(sampleTodo)
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