package com.aristhewonder.todolistapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aristhewonder.todolistapp.data.dao.TaskDao
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TaskCategory::class, Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        private val coroutineScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().taskDao()
            coroutineScope.launch {
                dao.insertCategory(TaskCategory(name = "Stared", reserved = true))
                dao.insertCategory(TaskCategory(name = "My Tasks"))

                val id1 = dao.insertCategory(TaskCategory(name = "List #1"))
                val id2 = dao.insertCategory(TaskCategory(name = "List #2"))
                val id3 = dao.insertCategory(TaskCategory(name = "List #3"))

                dao.insertTask(Task(name = "Task #1", categoryId = id1))

                dao.insertTask(Task(name = "Task #1", categoryId = id2))
                dao.insertTask(Task(name = "Task #2", categoryId = id2))

                dao.insertTask(Task(name = "Task #1", categoryId = id3))
                dao.insertTask(Task(name = "Task #2", categoryId = id3))
                dao.insertTask(Task(name = "Task #3", categoryId = id3))

            }
        }
    }

}