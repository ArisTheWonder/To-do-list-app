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
import com.aristhewonder.todolistapp.util.extension.filterByCategoryId
import com.aristhewonder.todolistapp.util.extension.filterByStared
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        const val STARED_TASK_CATEGORY_ID = 1L
    }

    private val eventsChannel = Channel<Events>()
    val events = eventsChannel.receiveAsFlow()

    private val _taskCategories = mutableStateOf<List<TaskCategory>>(emptyList())

    private val _selectedTaskCategory = mutableStateOf<TaskCategory?>(null)
    val selectedTaskCategory: State<TaskCategory?> = _selectedTaskCategory

    private val _reserved = mutableStateOf(false)
    val reserved: State<Boolean> = _reserved

    private val _selectedIndex = mutableStateOf<Int?>(null)

    private fun sendEvent(event: Events) = viewModelScope.launch {
        eventsChannel.send(event)
    }

    private fun changeIndex(index: Int) {
        if (index != _selectedIndex.value) {
            _selectedIndex.value = index
        }
    }

    fun onCategoryChanged(newIndex: Int) {
        updateTaskCategoryAndIndex(newIndex)
    }

    private fun updateTaskCategoryAndIndex(index: Int) {
        val taskCategory = _taskCategories.value[index]
        _selectedTaskCategory.value = taskCategory
        _reserved.value = taskCategory.reserved
        updateSelectedTaskCategoryIndex(index = index)
    }

    fun onCreateView() {
        viewModelScope.launch {
            sendEvent(Events.Loading)
            combine(
                preferencesManager.preferencesFlow,
                repository.getAllTaskCategory(),
                repository.getAllTasks()
            ) { userPreferences, taskCategories, tasks ->
                val index = userPreferences.selectedTaskCategoryIndex
                if (index > taskCategories.size - 1) {
                    return@combine
                }
                changeIndex(index = index)

                with(taskCategories) {
                    if (isNotEmpty()) {
                        _taskCategories.value = this
                        val categoryId = taskCategories[index].categoryId
                        val stared = categoryId == STARED_TASK_CATEGORY_ID
                        val filteredTasks = if (stared)
                            tasks.filterByStared()
                        else
                            tasks.filterByCategoryId(categoryId)
                        sendEvent(
                            Events.DataUpdated(
                                taskCategories = this,
                                tasks = filteredTasks,
                                index = index,
                                staredTasks = stared
                            )
                        )
                    }
                }
            }.collect()
        }
    }

    private fun updateSelectedTaskCategoryIndex(index: Int) {
        viewModelScope.launch {
            preferencesManager.setSelectedTaskCategoryIndex(index)
        }
    }

    fun onDeleteTaskCategory() {
        val taskCategory = _selectedTaskCategory.value!!
        val index = _selectedIndex.value!! - 1
        deleteTaskCategory(taskCategory)
        updateTaskCategoryAndIndex(index)
    }

    fun onTaskStarStatusChanged(task: Task, stared: Boolean) {
        updateTask(task.copy(stared = stared))
    }

    fun onInsertTask(taskName: String, categoryId: Long, stared: Boolean) {
        viewModelScope.launch {
            insertTask(
                task = Task(name = taskName, categoryId = categoryId, stared = stared)
            )
        }
    }

    fun onTaskCompleted(task: Task) {
        updateTask(task.copy(completed = true))
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

    sealed class Events {
        object Idle : Events()
        object Loading : Events()
        data class DataUpdated(
            val taskCategories: List<TaskCategory>,
            val tasks: List<Task>,
            val index: Int,
            val staredTasks: Boolean
        ) : Events()
    }
}