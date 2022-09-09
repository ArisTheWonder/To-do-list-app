package com.aristhewonder.todolistapp.ui.tasks

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import com.aristhewonder.todolistapp.util.PreferencesManager
import com.aristhewonder.todolistapp.util.extension.filterTasksByCategoryId
import com.aristhewonder.todolistapp.util.extension.filterTasksByStared
import com.aristhewonder.todolistapp.util.extension.isNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        const val STARED_CATEGORY = 1L
        const val TAG = "TaskViewModel"
    }

    //-------------------------------
    init {
        viewModelScope.launch {
            repository.getAllTaskCategory().collectLatest {
                categories.value = it
                Log.i(TAG, "${categories.value.size}")
            }

            preferencesManager.preferencesFlow.collectLatest {
                Log.i(TAG, "index: ${it.selectedTaskCategoryIndex}")
                selectCategory(categories.value[it.selectedTaskCategoryIndex])
            }

        }
        //getAll()
    }

    val categories = mutableStateOf<List<TaskCategory>>(emptyList())
    var selectedCategory = mutableStateOf<TaskCategory?>(null)
    var reservedCategory = mutableStateOf(true)
    val tasks: MutableState<List<Task>>
        get() {
            if (selectedCategory.value.isNull()) {
                return mutableStateOf(emptyList())
            }
            val selectedCategoryId = selectedCategory.value!!.categoryId
            val staredCategorySelected = selectedCategoryId == STARED_CATEGORY
            with(_tasks.value) {
                return if (staredCategorySelected) {
                    mutableStateOf(this.filterTasksByStared())
                } else {
                    mutableStateOf(this.filterTasksByCategoryId(selectedCategoryId))
                }
            }
        }
    private val _tasks = mutableStateOf<List<Task>>(emptyList())

    fun onCategorySelected(category: TaskCategory) {
        selectCategory(category)
        val selectedIndex = categories.value.indexOf(category)
        saveSelectedIndex(index = selectedIndex)
    }

    fun onDeleteSelectedTaskCategory() {
        this.selectedCategory.value?.let { taskCategory ->
            updateSelectedTaskCategoryIndex(categories.value.lastIndex - 1)
            deleteTaskCategory(taskCategory)
        }
    }

    fun onInsertTask(taskName: String, categoryId: Long) {
        val newTask = Task(name = taskName, categoryId = categoryId)
        insertTask(task = newTask)
    }

    fun onTaskCompleted(task: Task) {
        updateTask(task = task.copy(completed = true))
    }

    fun onTaskStaredChanged(task: Task, stared: Boolean) {
        updateTask(task = task.copy(stared = stared))
    }

    private fun getAll() {
        viewModelScope.launch {
            combine(
                repository.getAll(),
                preferencesManager.preferencesFlow
            ) { categoriesWithTasks, userPreferences ->
                categories.value = categoriesWithTasks.map { it.category }
                val index = userPreferences.selectedTaskCategoryIndex
                if (categories.value.size - 1 < index) {
                    updateSelectedTaskCategoryIndex(index - 1)
                }
                else if(categories.value.isNotEmpty()) {
                    selectCategory(category = getTaskCategory(index))
                }
                _tasks.value = categoriesWithTasks.map { it.tasks }.flatten()
            }.collect()
        }
    }

    private fun getTaskCategory(
        earlierSavedIndex: Int
    ): TaskCategory {
        val selectedIndexHasSavedBefore = earlierSavedIndex != -1
        return if (selectedIndexHasSavedBefore) {
            categories.value[earlierSavedIndex]
        } else {
            categories.value.last()
        }
    }

    private fun selectCategory(category: TaskCategory) {
        this.selectedCategory.value = category
        reservedCategory.value = category.reserved
    }

    private fun saveSelectedIndex(index: Int) {
        updateSelectedTaskCategoryIndex(index = index)
    }

    private fun updateSelectedTaskCategoryIndex(index: Int) {
        viewModelScope.launch {
            preferencesManager.updateSelectedTaskCategoryIndex(index)
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    private fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    private fun deleteTaskCategory(taskCategory: TaskCategory) {
        viewModelScope.launch {
            repository.deleteCategory(category = taskCategory)
        }
    }

}