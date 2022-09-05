package com.aristhewonder.todolistapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithTasks(
    @Embedded val category: TaskCategory,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    ) val tasks: List<Task>
)