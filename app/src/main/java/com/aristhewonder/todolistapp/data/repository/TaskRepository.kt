package com.aristhewonder.todolistapp.data.repository

import com.aristhewonder.todolistapp.data.dao.TaskDao
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao){

    fun getAll() = taskDao.getCategoriesWithTasks()
}