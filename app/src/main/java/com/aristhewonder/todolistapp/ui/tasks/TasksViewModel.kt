package com.aristhewonder.todolistapp.ui.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import com.aristhewonder.todolistapp.util.PreferencesManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _taskCategories = mutableStateOf<List<TaskCategory>>(emptyList())
    val taskCategories: State<List<TaskCategory>> = _taskCategories

    private val _selectedTaskCategory = mutableStateOf<TaskCategory?>(null)
    val selectedTaskCategory: State<TaskCategory?> = _selectedTaskCategory

    private val _reserved = mutableStateOf(false)
    val reserved: State<Boolean> = _reserved

    private val _tasksState = mutableStateOf<TasksState>(TasksState.Idle)
    val tasksState: State<TasksState> = _tasksState

    private val _selectedIndex = mutableStateOf(1)
    val selectedIndex: State<Int> = _selectedIndex

    init {

        viewModelScope.launch {
            combine(
                repository.getAllTaskCategory(),
                preferencesManager.preferencesFlow,
                repository.getAllTasks()
            ) { categories, userPreferences, tasks ->
                _taskCategories.value = categories
                userPreferences.selectedTaskCategoryIndex?.let { index ->
                    if (index < categories.size) {
                        val category = categories[index]
                        selectTaskCategory(category)
                        _selectedIndex.value = index
                        val staredCategory = category.categoryId == 1L
                        val filteredTasks = if (staredCategory) {
                            tasks.filter {
                                it.stared
                            }.filter {
                                !it.completed
                            }.sortedByDescending { it.creationDate }
                        } else {
                            tasks.filter {
                                it.categoryId == category.categoryId
                            }.filter {
                                !it.completed
                            }.sortedByDescending { it.creationDate }
                        }
                        _tasksState.value = if (filteredTasks.isEmpty()) {
                            TasksState.Empty(staredTasks = staredCategory)
                        } else {
                            TasksState.NotEmpty(tasks = filteredTasks, staredTasks = staredCategory)
                        }
                    }
                }
            }.collect()
        }
    }

    fun onTaskCategorySelected(taskCategory: TaskCategory, selectedIndex: Int) {
        if (selectedIndex == _selectedIndex.value) {
            return
        }
        selectTaskCategory(taskCategory)
        _tasksState.value = TasksState.Loading
        viewModelScope.launch {
            updateSelectedTaskCategoryIndex(selectedIndex)
        }
    }

    private fun updateSelectedTaskCategoryIndex(index: Int) {
        viewModelScope.launch {
            preferencesManager.updateSelectedTaskCategoryIndex(index)
        }
    }

    fun onDeleteTaskCategory() {
        _selectedTaskCategory.value?.let {
            val index = _selectedIndex.value - 1
            updateSelectedTaskCategoryIndex(index)
            _selectedIndex.value = index
            deleteTaskCategory(taskCategory = it)
        }
    }

    fun onTaskStarStatusChanged(task: Task, stared: Boolean) {
        updateTask(task.copy(stared = stared))
    }

    fun onInsertTask(taskName: String, categoryId: Long, stared: Boolean) {
        _tasksState.value = TasksState.InsertingNewTask
        viewModelScope.launch {
            insertTask(
                task = Task(name = taskName, categoryId = categoryId, stared = stared)
            )
        }
        _tasksState.value = TasksState.Idle
    }

    fun onTaskCompleted(task: Task) {
        updateTask(task.copy(completed = true))
    }

    private fun selectTaskCategory(taskCategory: TaskCategory) {
        _selectedTaskCategory.value = taskCategory
        _reserved.value = taskCategory.reserved
    }

    private fun deleteTaskCategory(taskCategory: TaskCategory) {
        viewModelScope.launch {
            repository.deleteCategory(taskCategory)
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    private suspend fun insertTask(task: Task) {
        repository.insertTask(task)
    }

    sealed class TasksState {
        object Idle : TasksState()
        object Loading : TasksState()
        object InsertingNewTask : TasksState()
        data class Empty(val staredTasks: Boolean) : TasksState()
        data class NotEmpty(val tasks: List<Task>, val staredTasks: Boolean) : TasksState()
    }

}