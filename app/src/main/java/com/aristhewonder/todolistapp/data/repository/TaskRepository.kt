package com.aristhewonder.todolistapp.data.repository

import com.aristhewonder.todolistapp.data.dao.TaskDao
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getAll() = taskDao.getCategoriesWithTasks()

    suspend fun insertCategory(category: TaskCategory) {
        taskDao.insertCategory(category)
    }

    suspend fun updateCategory(category: TaskCategory) {
        taskDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: TaskCategory) {
        taskDao.deleteCategory(category)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}