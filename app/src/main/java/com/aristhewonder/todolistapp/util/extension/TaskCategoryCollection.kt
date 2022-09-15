package com.aristhewonder.todolistapp.util.extension

import com.aristhewonder.todolistapp.R
import com.aristhewonder.todolistapp.data.entity.TaskCategory
import com.aristhewonder.todolistapp.ui.component.tablayout.TabItemModel

fun List<TaskCategory>.second(): TaskCategory {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return this[1]
}

fun List<TaskCategory>.secondOrFirst(): TaskCategory {
    if (isEmpty())
        throw NoSuchElementException("List is empty.")
    return if (this.size == 1)
        this.first()
    else
        this.second()
}

fun List<TaskCategory>.asTabItems(): List<TabItemModel> {
    return this.subList(1, this.size).map { taskCategory ->
            TabItemModel(text = taskCategory.name)
        }.toMutableList().also {
        it.add(
            0,
            TabItemModel(icon = R.drawable.star_filled)
        )
    }
}