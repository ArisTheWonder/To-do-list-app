package com.aristhewonder.todolistapp.ui.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    companion object {
        const val TAG = "TaskViewModel"
    }

    private val _taskCategories = mutableStateOf<List<TaskCategory>>(emptyList())
    val taskCategories: State<List<TaskCategory>> = _taskCategories

    private val _selectedTaskCategory = mutableStateOf<TaskCategory?>(null)
    val selectedTaskCategory: State<TaskCategory?> = _selectedTaskCategory

    private val _selectedIndex = mutableStateOf(1)
    val selectedIndex: State<Int> = _selectedIndex

    init {
        viewModelScope.launch {
            combine(
                repository.getAllTaskCategory(),
                preferencesManager.preferencesFlow
            ) { categories, userPreferences ->
                _taskCategories.value = categories
                userPreferences.selectedTaskCategoryIndex?.let {
                    selectTaskCategory(categories[it])
                    _selectedIndex.value = it
                }
            }.collect()
        }
    }

    fun onTaskCategorySelected(taskCategory: TaskCategory, selectedIndex: Int) {
        selectTaskCategory(taskCategory)
        viewModelScope.launch {
            preferencesManager.updateSelectedTaskCategoryIndex(selectedIndex)
        }
    }

    private fun selectTaskCategory(taskCategory: TaskCategory) {
        _selectedTaskCategory.value = taskCategory
    }

}