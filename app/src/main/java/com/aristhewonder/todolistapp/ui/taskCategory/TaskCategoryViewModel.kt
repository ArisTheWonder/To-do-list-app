package com.aristhewonder.todolistapp.ui.taskCategory

import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class TaskCategoryViewModel @ViewModelInject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = mutableStateOf<TaskCategoryState>(TaskCategoryState.IdleState)
    val state: TaskCategoryState
        get() = _state.value

    val enteredCategoryName = mutableStateOf("")

    fun onCategoryNameChanged(name: String) {
        enteredCategoryName.value = name
    }

    fun onInsert(categoryName: String) {
        viewModelScope.launch {
            _state.value = TaskCategoryState.LoadingState
            repository.insertCategory(TaskCategory(name = categoryName))
            _state.value = TaskCategoryState.SuccessState
        }
    }

    fun onUpdate(taskCategory: TaskCategory) {
        viewModelScope.launch {
            _state.value = TaskCategoryState.LoadingState
            repository.updateCategory(taskCategory)
            _state.value = TaskCategoryState.SuccessState
        }
    }

    sealed class TaskCategoryState {
        object SuccessState : TaskCategoryState()
        object FailureState : TaskCategoryState()
        object LoadingState : TaskCategoryState()
        object IdleState : TaskCategoryState()
    }
}