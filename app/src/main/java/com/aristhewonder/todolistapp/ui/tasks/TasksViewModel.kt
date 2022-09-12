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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        const val TAG = "TaskViewModel"
    }

    private val _taskCategories = mutableStateOf<List<TaskCategory>>(emptyList())
    val taskCategories: State<List<TaskCategory>> = _taskCategories

    private val _selectedTaskCategory = mutableStateOf<TaskCategory?>(null)

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _selectedIndex = mutableStateOf(1)
    val selectedIndex: State<Int> = _selectedIndex

    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: State<List<Task>> = _tasks

    init {

        viewModelScope.launch {
            combine(
                repository.getAllTaskCategory(),
                preferencesManager.preferencesFlow,
                repository.getAllTasks()
            ) { categories, userPreferences, tasks->
                _taskCategories.value = categories
                userPreferences.selectedTaskCategoryIndex?.let { index ->
                    val category = categories[index]
                    selectTaskCategory(category)
                    _selectedIndex.value = index
                    _tasks.value = tasks.filter{
                        it.categoryId == category.categoryId
                    }
                    _loading.value = false
                }
            }.collect()
        }
    }

    fun onTaskCategorySelected(taskCategory: TaskCategory, selectedIndex: Int) {
        selectTaskCategory(taskCategory)
        _loading.value = true
        viewModelScope.launch {
            preferencesManager.updateSelectedTaskCategoryIndex(selectedIndex)
        }
    }

    private fun selectTaskCategory(taskCategory: TaskCategory) {
        _selectedTaskCategory.value = taskCategory
    }


}