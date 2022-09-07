package com.aristhewonder.todolistapp.ui.tasks

import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    companion object {
        const val STARED_CATEGORY = 1L
    }

    private val categoriesWithTasks by lazy { repository.getAll() }
    val categories by lazy {
        categoriesWithTasks.map { items ->
            items.map {
                it.category
            }
        }
    }
    var selectedCategory = mutableStateOf<TaskCategory?>(null)
    val tasks: Flow<List<Task>>
        get() {
            val selectedCategoryId = selectedCategory.value!!.categoryId
            val staredCategorySelected = selectedCategoryId == STARED_CATEGORY

            return if (staredCategorySelected) {
                filterTasksByStared()
            } else {
                filterTasksByCategoryId(categoryId = selectedCategoryId)
            }
        }

    fun selectCategory(category: TaskCategory) {
        this.selectedCategory.value = category
    }

    fun insertTaskCategory(categoryName: String) {
        viewModelScope.launch {
            repository.insertCategory(TaskCategory(name = categoryName))
        }
    }

    fun insertTask(taskName: String, categoryId: Long) {
        viewModelScope.launch {
            repository.insertTask(Task(name = taskName, categoryId = categoryId))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    private fun filterTasksByStared(): Flow<List<Task>> =
        categoriesWithTasks.map { items ->
            items.asSequence().map {
                it.tasks
            }.flatten()
                .filter { it.stared }
                .filter { !it.completed }
                .sortedByDescending { it.creationDateFormatted }.toList()
        }

    private fun filterTasksByCategoryId(categoryId: Long): Flow<List<Task>> =
        categoriesWithTasks.map { items ->
            items.first {
                it.category.categoryId == categoryId
            }.tasks.filter { !it.completed }.sortedByDescending { it.creationDateFormatted }
        }

}