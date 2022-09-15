package com.aristhewonder.todolistapp.util.extension

import com.aristhewonder.todolistapp.data.entity.Task

fun Collection<Task>.filterByStared(): List<Task> =
    this.filter { it.stared }
        .filter { !it.completed }
        .sortedByDescending { it.creationDateFormatted }

fun Collection<Task>.filterByCategoryId(categoryId: Long): List<Task> =
    this.filter { it.categoryId == categoryId }
        .filter { !it.completed }
        .sortedByDescending { it.creationDateFormatted }
