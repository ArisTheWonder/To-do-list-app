package com.aristhewonder.todolistapp.ui.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristhewonder.todolistapp.data.entity.Task
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import com.aristhewonder.todolistapp.util.Keys
import com.aristhewonder.todolistapp.util.extension.orFalse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditTaskViewModel @ViewModelInject constructor(
    private val repository: TaskRepository,
    @Assisted private val saveStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val tasksEvent = taskEventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val taskId: Long = saveStateHandle.get<Long>(Keys.TASK_ID) ?: -1
            repository.getTaskById(taskId).collect {
                _task.value = it
                taskName.value = it.name
            }
        }
    }

    private val _task = mutableStateOf<Task?>(null)
    val task: State<Task?> = _task
    val taskName = mutableStateOf("")


    fun onTaskStaredStatusChanged(stared: Boolean) {
        _task.value?.let {
            updateTask(task = it.copy(stared = stared))
        }
    }

    fun onTaskCompleted() {
        _task.value?.let {
            updateTask(task = it.copy(completed = true))
            sendEvent(event = TaskEvent.Finish)
        }
    }

    fun onTaskNameChanged() {
        _task.value?.let {
            updateTask(task = it.copy(name = taskName.value))
        }
    }

    fun onDeleteTask() {
        _task.value?.let {
            deleteTask(task = it)
            sendEvent(event = TaskEvent.Finish)
        }
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    private fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    private fun sendEvent(event: TaskEvent) = viewModelScope.launch {
        taskEventChannel.send(event)
    }

    sealed class TaskEvent {
        object Finish : TaskEvent()
    }

}