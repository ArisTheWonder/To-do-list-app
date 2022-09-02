package com.aristhewonder.todolistapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aristhewonder.todolistapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


class TasksViewModel @ViewModelInject constructor(
    private val repository: TaskRepository
    ): ViewModel() {

    val categoriesWithTasks = repository.getAll()
}