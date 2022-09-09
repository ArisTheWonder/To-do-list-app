package com.aristhewonder.todolistapp.util.extension

import com.aristhewonder.todolistapp.data.entity.Task

fun Collection<Task>.filterTasksByStared(): List<Task> =
    this.filter { it.stared }
        .filter { !it.completed }
        .sortedByDescending { it.creationDateFormatted }
        .toList()

fun Collection<Task>.filterTasksByCategoryId(categoryId: Long): List<Task> =
    this.filter { it.categoryId == categoryId }
        .filter { !it.completed }
        .sortedByDescending { it.creationDateFormatted }