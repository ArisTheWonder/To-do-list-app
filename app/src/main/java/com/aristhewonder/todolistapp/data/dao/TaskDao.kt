package com.aristhewonder.todolistapp.data.dao

import androidx.room.*
import com.aristhewonder.todolistapp.data.entity.CategoryWithTasks
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM task_category_table")
    fun getCategoriesWithTasks(): Flow<List<CategoryWithTasks>>

    @Transaction
    @Query("SELECT * FROM task_category_table")
    fun getAllTaskCategory(): Flow<List<TaskCategory>>

    @Transaction
    @Query("SELECT * FROM task_table WHERE categoryId = :categoryId")
    fun getTasksByCategoryId(categoryId: Long): Flow<List<Task>>

    @Transaction
    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    fun getTaskById(taskId: Long): Flow<Task>

    @Transaction
    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TaskCategory): Long

    @Update
    suspend fun updateCategory(category: TaskCategory)

    @Delete
    suspend fun deleteCategory(category: TaskCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

}